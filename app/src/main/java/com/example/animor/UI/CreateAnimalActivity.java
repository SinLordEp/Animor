package com.example.animor.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.animor.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

    public class CreateAnimalActivity extends AppCompatActivity {

        private EditText etFecha;
        private Calendar calendar;
        private SimpleDateFormat dateFormatter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_animal);

            // Inicializar componentes
            etFecha = findViewById(R.id.etFecha);
            calendar = Calendar.getInstance();
            dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Configurar el listener para el EditText
            etFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog();
                }
            });
        }

        /**
         * HAcer que aparezca el calendario para elegir fecha de nacimiento
         */
        private void showDatePickerDialog() {
            // Obtener la fecha actual para mostrar en el picker
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Crear el DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(android.widget.DatePicker view, int selectedYear,
                                              int selectedMonth, int selectedDay) {
                            // Actualizar el calendario con la fecha seleccionada
                            calendar.set(selectedYear, selectedMonth, selectedDay);

                            // Formatear la fecha y mostrarla en el EditText
                            String formattedDate = dateFormatter.format(calendar.getTime());
                            etFecha.setText(formattedDate);
                        }
                    },
                    year, month, day);
            // Establecer fecha mínima (hoy)
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            // O para establecer fecha máxima (ej. hoy + 1 año)
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.YEAR, 1);
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            // Mostrar el diálogo
            datePickerDialog.show();
        }
    }