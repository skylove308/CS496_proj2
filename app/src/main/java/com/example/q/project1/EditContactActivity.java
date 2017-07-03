package com.example.q.project1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditContactActivity extends AppCompatActivity {

    /* ---------------- */
    /* MACROS & GLOBALS */
    /* ---------------- */

    /* globals variables */
    private int index = -1;


    /* ------------- */
    /* MAIN FUNCTION */
    /* ------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* get variables from previous activity */
        index = getIntent().getIntExtra("index", -1);
        String name = getIntent().getStringExtra("name");
        String number = getIntent().getStringExtra("number");

        /* set EditText texts as previous values */
        EditText inputName = (EditText) findViewById(R.id.inputName);
        EditText inputNumber = (EditText) findViewById(R.id.inputNumber);
        inputName.setText(name); // removed unformatName
        inputNumber.setText(number.replaceAll("-", ""));

        /* listener for submit button */
        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(submitListener);

        /* listener for cancel button */
        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(cancelListener);
    }


    /* ---------------- */
    /* HELPER FUNCTIONS */
    /* ---------------- */

    /* FUNCTION: checks if name has more than one word */
    public boolean checkName(String name) {
        String[] tokens = name.split(" ");
        return tokens.length > 1;
    }

    /* FUNCTION: checks number is 11 numbers long */
    public boolean checkNumber(String number) {
        return number.length() == 11;
    }

    /* FUNCTION: formats name to first name first */
    public String unformatName(String name) {
        String[] tokens = name.replaceAll(",", "").split("[ ]+");
        String result = tokens[1] + " " + tokens[0];
        return result;
    }


    /* --------- */
    /* LISTENERS */
    /* --------- */

    /* LISTENER: clicking submit invokes MainActivity */
    View.OnClickListener submitListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String inputName = ((EditText) findViewById(R.id.inputName)).getText().toString();
            String inputNumber = ((EditText) findViewById(R.id.inputNumber)).getText().toString();

            if (inputName.length() == 0) {
                Toast.makeText(getApplicationContext(), "Please input a valid name.", Toast.LENGTH_LONG).show();
                return;
            }

            if (checkNumber(inputNumber) == false) {
                Toast.makeText(getApplicationContext(), "Please input a valid number.", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("newName", inputName);
            intent.putExtra("newNumber", inputNumber);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /* LISTENER: clicking submit returns to MainActivity */
    View.OnClickListener cancelListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }
    };
}
