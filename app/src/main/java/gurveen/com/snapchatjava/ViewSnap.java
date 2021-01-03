package gurveen.com.snapchatjava;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ViewSnap extends AppCompatActivity {

    TextView messageTextView;
    ImageView snapImageView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);

        messageTextView = findViewById(R.id.messageTextView);
        snapImageView = findViewById(R.id.snapImageView);

        messageTextView.setText(getIntent().getStringExtra("message"));

        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;

        try {
            myImage = task.execute(getIntent().getStringExtra("imageUrl")).get();

            snapImageView.setImageBitmap(myImage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

                httpsURLConnection.connect();

                InputStream is = httpsURLConnection.getInputStream();

                Bitmap myBitmap =  BitmapFactory.decodeStream(is);

                return myBitmap;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("snaps").child(getIntent().getStringExtra("snapKey"))
                .removeValue();

        FirebaseStorage.getInstance().getReference().child("images")
                .child(getIntent().getStringExtra("imageName")).delete();
    }
}
