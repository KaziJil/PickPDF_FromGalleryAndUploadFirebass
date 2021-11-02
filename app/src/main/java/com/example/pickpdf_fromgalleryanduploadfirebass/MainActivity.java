package com.example.pickpdf_fromgalleryanduploadfirebass;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button pickPDF,previus,next;
    private TextView pageNumber;
    private ImageView pdfview;
    PdfRenderer renderer;
    int total_page=0;
    int displayPage=0;
    public static final int PICK_FILE=99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pickPDF=findViewById(R.id.pick_pdf);
        previus=findViewById(R.id.pdf_previusButton);
        pageNumber=findViewById(R.id.pdf_page);
        next=findViewById(R.id.pdf_nextButton);
        pdfview=findViewById(R.id.pdf_imageView);
        pickPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent,PICK_FILE);
            }
        });
        previus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayPage > 0){
                    displayPage--;
                    displayMethods(displayPage);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayPage<(total_page-1)){
                    displayPage++;
                    displayMethods(displayPage);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_FILE && resultCode==RESULT_OK){
            if (data!=null){
                uploadFile(data.getData());
                Uri uri=data.getData();
                try {
                    ParcelFileDescriptor parcelFileDescriptor=getContentResolver().openFileDescriptor(uri,"r");
                    renderer=new PdfRenderer(parcelFileDescriptor);
                    total_page=renderer.getPageCount();
                    displayPage=0;
                    displayMethods(displayPage);
                }catch (FileNotFoundException file){

                }catch (IOException e){

                }
            }
        }
    }

    private void uploadFile(Uri data) {

    }

    private void displayMethods(int _number) {
        if (renderer!=null){
            PdfRenderer.Page page=renderer.openPage(_number);
            Bitmap bitmap=Bitmap.createBitmap(page.getWidth(),page.getHeight(),Bitmap.Config.ARGB_8888);
            page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pdfview.setImageBitmap(bitmap);
            page.close();
            pageNumber.setText((_number+1) + "/" + total_page);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (renderer!=null){
            renderer.close();
        }
    }
}