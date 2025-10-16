package com.spencer.therapynotestracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.spencer.therapynotestracker.database.Session;

public class PdfUtils {

    public static PdfDocument buildExportPDFString(Context context, View view) {

        EditText editAgenda = view.findViewById(R.id.edit_agenda);
        EditText editNotes = view.findViewById(R.id.edit_notes);
        TextView date = view.findViewById(R.id.view_date);
        EditText editTherapist = view.findViewById(R.id.edit_therapist);

        String dateDate = date.getText().toString();
        String newAgenda = editAgenda.getText().toString();
        String newNotes = editNotes.getText().toString();
        String newTherapist = editTherapist.getText().toString();

        Session sesh = new Session(dateDate, newAgenda, newNotes, newTherapist);

        // declaring width and height
        // for our PDF file.
        int pageHeight = 1120;
        int pagewidth = 792;

        // creating an object variable for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used for drawing shapes and we will use "title" for adding text in our PDF file.
        Paint title = new Paint();

        // adding page info to our PDF file in which we will be passing our pageWidth, pageHeight and number of pages and after that we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(context, R.color.black));

        String concat = sesh.getDate() + " " + sesh.getTherapist() + "\nAgenda: " + sesh.getAgenda() + "\nNotes: " + sesh.getNotes();

        TextPaint mTextPaint = new TextPaint();
        StaticLayout mTextLayout = new StaticLayout( concat ,mTextPaint, canvas.getWidth() - 100, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        int textX = 50;
        int textY = 500;
        canvas.translate(textX, textY);

        mTextLayout.draw(canvas);
        canvas.restore();

        // creating another text and in this we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(context, R.color.black));
        title.setTextSize(15);

        // finishing our page.
        pdfDocument.finishPage(myPage);

        return pdfDocument;
    }
}
