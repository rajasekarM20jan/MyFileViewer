package com.example.myfileviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public static final int PICKFILE_RESULT_CODE=100;
    Button button;
    TextView fileName;
    File source,destination;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=findViewById(R.id.button);
        fileName=findViewById(R.id.fileName);
        context=this;


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                    chooseFile.setType("*/*");
                    startActivityForResult(
                            Intent.createChooser(chooseFile, "Choose a file"),
                            PICKFILE_RESULT_CODE);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){

            try {
                Uri selectedFileUri = data.getData();



                File destinationDirectory=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/data/com.example.myfileviewer/files/documents");
                if(!destinationDirectory.exists()){
                    destinationDirectory.mkdirs();
                }
                destination = new File(destinationDirectory + "/" + "demo.pdf");
                Log.d("Destination is ", destination.toString());

                ContentResolver cr = context.getContentResolver();
                InputStream inputStream = cr.openInputStream(selectedFileUri);

                FileOutputStream outputStream = new FileOutputStream(destination);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                openPDF(destination);


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void openPDF(File fileFromList) {
        try {
            // Get the File location and file name.
            File file = fileFromList;
            Log.d("pdfFIle", "" + file);

            // Get the URI Path of file.
            Uri uriPdfPath = FileProvider.getUriForFile(context, getApplicationContext().getPackageName()+".fileprovider", file);
            Log.d("pdfPath", "" + uriPdfPath);
            String type;

            if(uriPdfPath.toString().contains(".jpg")||uriPdfPath.toString().contains(".jpeg")||uriPdfPath.toString().contains(".png")){
                type="image/*";
            }else if(uriPdfPath.toString().contains(".pdf")){
                type="application/pdf";
            }else{
                type="*/*";
            }

            // Start Intent to View PDF from the Installed Applications.
            Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
            pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenIntent.setClipData(ClipData.newRawUri("", uriPdfPath));
            pdfOpenIntent.setDataAndType(uriPdfPath, type);
            pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {
                startActivity(pdfOpenIntent);
            } catch (ActivityNotFoundException activityNotFoundException) {
                Toast.makeText(context, "There is no app to load corresponding PDF", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}