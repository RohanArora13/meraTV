package channel.price.calculator.meratvapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import channel.price.calculator.meratvapp.json_single.db.SingleDbHelper;
import channel.price.calculator.meratvapp.selection.SelectionDbHelper;

public class PDFGenerator {
    private static final String TAG = "PDFGenerator";

    Double base_price_main = 130.00;  // base price
    Double maintanence_price_main = 0.00; // maintanence price
    Integer slot_price = 0; // slot extenstion price
    Double gst_charges = 0.00;
    Double channel_price = 0.00; // slot extenstion price
    Double total_price = 0.00;
    Integer no_of_channels = 0,available_channel = 100;
    Integer  total_available_channel = 100;

    Integer free = 0,paid = 0 ;
    Double total = 0.00;

    private Integer current_selection;

    private Context context;

    /***
     * Variables for further use....
     */
    BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
    float mHeadingFontSize = 20.0f;
    float mValueFontSize = 26.0f;

    /**
     * How to USE FONT....
     */
   // BaseFont urName = BaseFont.createFont("res/fonts/roboto-regular.ttf", "UTF-8", BaseFont.EMBEDDED);

    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    public PDFGenerator(Context context) throws IOException, DocumentException {
        this.context = context;
    }

    public void createPdf(Integer current_selection_no) {
        String filename = "channel_list";
        current_selection=current_selection_no;
        new calculate().execute();

        if (writeToFile(filename)) {
            Toast.makeText(context.getApplicationContext(),
                    filename + ".pdf created", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(context.getApplicationContext(), "I/O error",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private boolean writeToFile(String fname) {
        try {
            // path to the file directory
            String fpath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + context.getString(R.string.folder_name);

            File dir = new File(fpath);

            // create new directory if not exist
            dir.mkdir();

            // now create pdf file in file directory
            File file = new File(dir, fname + ".pdf");

            // delete older file if already exist to reduce storage uses
            if (file.exists()) {
                file.delete();
            }

            // create new file (return false if unable to create file)
            if (!file.createNewFile()) {
                Log.d(TAG, "writeToFile: Unable to create file");
                return false;
            }

            // get instance of new document form itextpdf library
            Document document = new Document();

            // get instance of PdfWriter
            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
            // open document to write into it
            document.open();

            // set basic details
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("MeraTV app");
            //todo add appurl
            document.addCreator("MeraTV");

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            /** Title Order Details... **/
            // Adding Title....
          //  Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Font font_title = new Font(Font.FontFamily.HELVETICA, 13);
            // Creating Chunk
            Chunk mOrderDetailsTitleChunk = new Chunk("Selection Details",font_title);

            // Creating Paragraph to add...
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);

            // Setting Alignment for Heading
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);

            // Finally Adding that Chunk
            document.add(mOrderDetailsTitleParagraph);
            DecimalFormat precision = new DecimalFormat("0.00");

            Log.d("PDGGenerator","available_channel = "+available_channel);
            Log.d("PDGGenerator","total_available_channel = "+total_available_channel);
            //Log.d("PDGGenerator","total_price = "+total_price);
            Log.d("PDGGenerator","no_of_channels = "+no_of_channels);

            Log.d("PDGGenerator","base_price_main = "+base_price_main);
            Log.d("PDGGenerator","maintanence_price_main = "+maintanence_price_main);
            Log.d("PDGGenerator","slot_price = "+channel_price);
            Log.d("PDGGenerator","total_price = "+total_price);
            Log.d("PDGGenerator","gst_charges = "+gst_charges);

            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Channel selected : "+Integer.toString(no_of_channels)));
            document.add(new Paragraph("Free Channel : "+Integer.toString(free)));
            document.add(new Paragraph("Paid Channel : "+Integer.toString(paid)));
            document.add(new Paragraph("Total channel slot : "+Integer.toString(total_available_channel)));
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            /** Fields of Order Details... **/
            document.add(new Paragraph("Base Price : "+precision.format(base_price_main).toString()));
            document.add(new Paragraph("Slot Extension Price : "+precision.format(slot_price).toString()));
            document.add(new Paragraph("Total channel Price : "+precision.format(channel_price).toString()));
            document.add(new Paragraph("Maintenance charge : "+precision.format(maintanence_price_main).toString()));
            document.add(new Paragraph("GST charge : "+precision.format(gst_charges).toString()));
            document.add(new Paragraph("calculation : "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Price : "+precision.format(total_price).toString()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(""));
            // Creating Chunk
            Chunk mchaanelDetailsTitleChunk = new Chunk("Channel Details",font_title);

            // Creating Paragraph to add...
            Paragraph mchannelDetailsTitleParagraph = new Paragraph(mchaanelDetailsTitleChunk);

            // Setting Alignment for Heading
            mchannelDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);

            // Finally Adding that Chunk
            document.add(mchannelDetailsTitleParagraph);
            document.add(new Chunk(lineSeparator));

            // Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            Font font_table_head = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
            // Header
            PdfPCell cell1 = new PdfPCell(new Phrase("Channel Name",font_table_head));
            PdfPCell cell2 = new PdfPCell(new Phrase("Price (Rs)",font_table_head));
            //cell1.setColspan(4);
           // cell2.setColspan(4);

            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell1);
            table.addCell(cell2);

            // Data
           // if (c.moveToFirst()) {
          //      do {

           // current_selection= bundle.getInt("current_selection",2000);

            // calculating data from selection DB
            SelectionDbHelper sel = new SelectionDbHelper(context);
            //SingleDbHelper single_DB = new SingleDbHelper(getContext());

            ArrayList uidc_list = sel.get_uidc_from_selection(current_selection);

            Log.d("PDGGenerator","uidc_list.size()"+uidc_list.size());
            SingleDbHelper single_DB = new SingleDbHelper(context);
            // todo get data into bottom sheet
            for(int i=0;i<uidc_list.size();i++){

                Double price = single_DB.get_price (uidc_list.get(i).toString().trim());
                String channel_name = single_DB.get_name(uidc_list.get(i).toString().trim());

                Log.d("pdf_creation","uidc = "+uidc_list.get(i).toString().trim()+" price = "+single_DB.get_price (uidc_list.get(i).toString().trim()));
                cell2 = new PdfPCell(new Phrase(channel_name));
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPaddingTop(3f);
                cell1.setPaddingBottom(3f);
                table.addCell(cell2);

                cell1 = new PdfPCell(new Phrase(Double.toString(price)));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPaddingTop(5f);
                cell1.setPaddingBottom(5f);
                table.addCell(cell1);
            }

//            for (int i = 0; i <=5;i++) {
//                String group = "Channel Name";
//                cell1 = new PdfPCell(new Phrase(group));
//                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell1.setPaddingTop(5f);
//                cell1.setPaddingBottom(5f);
//                table.addCell(cell1);
//                String machine = "Price";
//                cell2 = new PdfPCell(new Phrase(machine));
//                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell1.setPaddingTop(3f);
//                cell1.setPaddingBottom(3f);
//                table.addCell(cell2);
//            }
              //  } while (c.moveToNext());
          //  }
            document.add(table);

            // channel list
            document.close();

            // share the pdf after pdf creation
            sharePdf(file);

            return true;
        } catch (IOException e) {
            Log.d(TAG, "writeToFile: " + e);
            return false;
        } catch (DocumentException e) {
            Log.d(TAG, "writeToFile: " + e);
            return false;
        }
    }

    private void sharePdf(File file) {
        // now to share file get uri of file
       // Uri uri = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), file);

        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context),
                BuildConfig.APPLICATION_ID + ".provider", file);

        // create an intent to share file
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(share);
    }

    public class calculate extends AsyncTask<Void,Void,Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {

            // calculating data from selection DB
            SelectionDbHelper sel = new SelectionDbHelper(context);
            //SingleDbHelper single_DB = new SingleDbHelper(getContext());

            ArrayList uidc_list = sel.get_uidc_from_selection(current_selection);

            Log.d("PDGGenerator","uidc_list.size()"+uidc_list.size());
            // todo get data into bottom sheet
            for(int i=0;i<uidc_list.size();i++){
                SingleDbHelper single_DB = new SingleDbHelper(context);
                Double price = single_DB.get_price (uidc_list.get(i).toString().trim());
                Log.d("PDGGenerator","uidc = "+uidc_list.get(i).toString().trim()+" price = "+single_DB.get_price (uidc_list.get(i).toString().trim()));
                if (price<=0.00){
                    free+=1;
                }else {
                    paid +=1;
                }
                channel_price = channel_price + price;

                no_of_channels = free+paid;
                Log.d("PDGGenerator","no_of_channels = "+no_of_channels);
                //no_of_channels=101;

                if(no_of_channels>total_available_channel){
                    total_available_channel+=25;
                    slot_price+=20;
                }
            }

            // todo calculate slots

            available_channel= total_available_channel - no_of_channels;
            total_price = base_price_main + maintanence_price_main + slot_price  + channel_price;

            // gst calculation
            gst_charges = (18*total_price) / 100 ;
            // adding gst charges
            total_price = total_price + gst_charges;

//            Log.d("PDGGenerator","available_channel = "+available_channel);
//            Log.d("PDGGenerator","total_available_channel = "+total_available_channel);
//            //Log.d("PDGGenerator","total_price = "+total_price);
//            Log.d("PDGGenerator","no_of_channels = "+no_of_channels);
//
//            Log.d("PDGGenerator","base_price_main = "+base_price_main);
//            Log.d("PDGGenerator","maintanence_price_main = "+maintanence_price_main);
//            Log.d("PDGGenerator","slot_price = "+channel_price);
//            Log.d("PDGGenerator","total_price = "+total_price);
//            Log.d("PDGGenerator","gst_charges = "+gst_charges);

            return true;
        }


        //        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
//        }
    }
}
