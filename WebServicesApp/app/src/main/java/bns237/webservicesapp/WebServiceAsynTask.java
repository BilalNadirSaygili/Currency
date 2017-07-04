package bns237.webservicesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by bns on 4.07.2017.
 */

public class WebServiceAsynTask extends AsyncTask<String, String, List<String>> {
    private Context context;
    private ListView listView;
    private ProgressDialog progressDialog;

    public WebServiceAsynTask(Context context) {
        this.context = context;
        listView = (ListView) ((AppCompatActivity) context).findViewById(R.id.listView);

    }

    /*UI Thread içerisinde yürütüldü...
    *Task çalışmadan önce yapılacak hazırlıklar burada yapıldı...*/
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Lütfen Bekleyiniz ...", "İşlem yürütülüyor...", true);
    }




    /*execute metoduna verilen arguman ile çağrıldı. Arkaplan işlemleri burada yapıldı.
    * geriye dönen değer 3. parametre tipinde ve onPostExecute metoduna arguman olarak verildi
    *UI Thread içinde değil yardımcı Thread içerisinde çalıştırıldı.
    *publishProgress metodu ile onProgressUpdate metoduna bilgi gönderildi*/

    @Override
    protected List<String> doInBackground(String... params) {
        List<String> currency_list = new ArrayList<String>();

        HttpURLConnection connection = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            int connection_status = connection.getResponseCode();
            if (connection_status == HttpURLConnection.HTTP_OK) {

                BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
                publishProgress("Döviz kurları okunuyor"); //ProgressDialog güncellemesi için bilgi gönderildi
                DocumentBuilderFactory dB_Factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = dB_Factory.newDocumentBuilder();
                Document document = documentBuilder.parse(stream);
                NodeList currencyNodeList = document.getElementsByTagName("Currency");
                for (int i = 0; i < currencyNodeList.getLength(); i++) {

                    Element element = (Element) currencyNodeList.item(i);
                    NodeList nodeListUnit = element.getElementsByTagName("Unit");
                    NodeList nodeListCurrencyName = element.getElementsByTagName("Isim");
                    NodeList nodeListBuying = element.getElementsByTagName("ForexBuying");
                    NodeList nodeListSelling = element.getElementsByTagName("ForexSelling");

                    String Unit = nodeListUnit.item(0).getFirstChild().getNodeValue();
                    String Name = nodeListCurrencyName.item(0).getFirstChild().getNodeValue();
                    String Buying = nodeListBuying.item(0).getFirstChild().getNodeValue();
                    String Selling = nodeListSelling.item(0).getFirstChild().getNodeValue();

                    currency_list.add(Unit + " " + Name + "  Alış:" + Buying + "  Satış:" + Selling);


                }
                publishProgress("Liste Güncelleniyor"); //ProgressDialog güncellemesi için bilgi gönderildi

            }
        } catch (Exception e) {
            Log.e("XML Parse Hatası", e.getMessage().toString());
        } finally {
            if (connection != null) {

                connection.disconnect();
            }
        }

        return currency_list;
    }
    /*UI Thread içerisinde yürütüldü...
     *ProgressDialog güncellendi
     *doInBackground içerisinden publishProgress metodu ile arguman gönderildi*/
    @Override
    protected void onProgressUpdate(String... values) {

        progressDialog.setMessage(values[0]);
    }

    //UI Thread içerisinde yürütüldü
    //Parametre olarak doInBackground metodunun sonucu alındı.
    @Override
    protected void onPostExecute(List<String> result) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, result);
        listView.setAdapter(adapter);
        progressDialog.cancel();
    }
}


