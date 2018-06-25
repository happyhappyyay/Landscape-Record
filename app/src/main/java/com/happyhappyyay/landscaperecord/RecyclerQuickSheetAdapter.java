package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerQuickSheetAdapter extends RecyclerView.Adapter {
    private String startDateString;
    private String endDateString;
    private List<Customer> customers;
    private AppDatabase db;
    private Customer customer;
    private Context context;

    public RecyclerQuickSheetAdapter (List<Customer> customers, Context context) {
        this.customers = customers;
        if (this.customers == null) {
            this.customers = new ArrayList<>();
        }
        db = AppDatabase.getAppDatabase(context);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        startDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date(System.currentTimeMillis()));
        endDateString = startDateString;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_sheet_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public LinearLayout linearLayout;
        private Button jobActionButton;
        private CheckBox checkBox1, checkBox2, checkBox3;
        List<CheckBox> checkBoxes;
        private EditText notes;



        public ListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.quick_sheet_address_text);
            linearLayout = view.findViewById(R.id.quick_sheet_layout);
            jobActionButton = view.findViewById(R.id.quick_sheet_button);
            jobActionButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            TODO: Move to quick sheet class?
                            boolean flagDifferentEndTime = false;
                            long startTime = 0;
                            long endTime = 0;
                            Service service = null;
//                            set entered/automatic dates into start and end dates
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                                try {
                                    Date startDate = dateFormat.parse(startDateString);
                                    Date endDate = dateFormat.parse(endDateString);
                                    startTime = startDate.getTime();
                                    endTime = endDate.getTime();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (!startDateString.equals(endDateString) || endTime < startTime) {
                                    flagDifferentEndTime = true;
                                }
                            String servicesString = "";
                            customer = customers.get(getAdapterPosition());
                            List<Service> services = customer.getCustomerServices();
                            int serviceListPosition = 0;
//                            check for service pause and date then set inputs to services string
                            for (int i = 0; i < services.size(); i++) {
                                Service s = services.get(i);
                                if (s.isPause()) {
                                    if (s.convertStartTimeToDateString().equals(startDateString)) {
                                        service = s;
                                        serviceListPosition = i;
                                        for (CheckBox c : checkBoxes) {
                                            if (c.isChecked()) {
                                                servicesString += c.getText().toString() + "#*#";
                                            }
                                        }
                                        if (!notes.getText().toString().isEmpty()) {
                                            servicesString += "Other " + notes.getText().toString() + "#*#";
                                        }
                                    }
                                }
                            }
//                            create new service if could not find existing service
                            if (service == null) {
                                jobActionButton.setText("Finish");
                                service = new Service();
                                service.setPause(true);
                                service.setStartTime(startTime);
                                service.setServices(servicesString);
                                customer.addService(service);
                                updateCustomer();
                            }
//                            otherwise update existing service
                            else {
                                if (!flagDifferentEndTime) {
                                    jobActionButton.setText("✓");
                                    service.setServices(servicesString);
                                    service.setEndTime(endTime);
                                    service.setPause(false);
                                    customer.updateService(service, serviceListPosition);
                                    updateCustomer();
                                }
                                else {
                                    Toast.makeText(context,
                                            "There was an error with the start - end date " +
                                                    "combination. Please check the dates are correct.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
            );
            checkBox1 = view.findViewById(R.id.quick_sheet_check_box1);
            checkBox2 = view.findViewById(R.id.quick_sheet_check_box2);
            checkBox3 = view.findViewById(R.id.quick_sheet_check_box3);
            notes = view.findViewById(R.id.quick_sheet_notes_text);
            checkBoxes = new ArrayList<>(Arrays.asList(checkBox1, checkBox2, checkBox3));
        }

        public void bindView(int position) {
            Customer customer = customers.get(position);
            name.setText(customer.getCustomerAddress());
            boolean existingService = false;
            if (customer.getCustomerServices() != null) {
                List<Service> services = customer.getCustomerServices();
                for (Service s : services) {
                    if (s.convertStartTimeToDateString().equals(startDateString)) {
                        existingService = true;
                        if (s.isPause()) {
                            jobActionButton.setText("Finish");
                        }
                        else {
                            jobActionButton.setText("✓");
                        }

                        for (CheckBox c : checkBoxes) {
                            if (s.getServices().contains(c.getText().toString())) {
                                c.setChecked(true);
                            }
                            else {
                                c.setChecked(false);
                            }
                        }
                        if (s.getServices().contains("Other ")) {
                            String otherString = "Other ";
                            int indexStart = s.getServices().indexOf(otherString) + otherString.length();
                            int indexEnd = s.getServices().indexOf("#*#", indexStart);
                            notes.setText(s.getServices().substring(indexStart, indexEnd));
                        }
                        else {
                            notes.setText("");
                        }
                    }
                    if (!existingService) {
                        jobActionButton.setText("Start");
                        for (CheckBox c : checkBoxes) {
                            c.setChecked(false);
                        }
                        notes.setText("");
                    }
                }
            }
        }
    }

    private void updateCustomer() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.customerDao().updateCustomer(customer);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
}