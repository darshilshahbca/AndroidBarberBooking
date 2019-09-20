package darshil.dev.androidbarberbooking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import darshil.dev.androidbarberbooking.Common.Common;

public class MainActivity extends AppCompatActivity {

    public static final int APP_REQUEST_CODE = 7117; //Any Number

    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.txt_skip)
    TextView txt_skip;

    @OnClick(R.id.btn_login)
    void loginUser()
    {
        final Intent intent = new Intent(this, AccountKitActivity.class );
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());

        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @OnClick(R.id.txt_skip)
    void skipLoginJustGoHome()
    {
         Intent intent =  new Intent (this, HomeActivity.class);
         intent.putExtra(Common.IS_LOGIN, false);
         startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == APP_REQUEST_CODE)
        {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(loginResult.getError() != null)
            {
                Toast.makeText(this, ""+loginResult.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();

            }
            else if (loginResult.wasCancelled())
            {
                Toast.makeText(this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Intent object that provides runtime binding between separate components, such as two activities.
                //Intent Starts Another Activity
                //Activity Class is a Subclass of Context
                Intent intent =  new Intent (this, HomeActivity.class);

                //Intent Can Carry data type as Key-Value pairs called EXTRAS
                intent.putExtra(Common.IS_LOGIN, true);

                //Starts instance of activity specified by theIntent
                startActivity(intent);


                finish();
            }

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                ).withListener(new MultiplePermissionsListener() {

            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                AccessToken accessToken = AccountKit.getCurrentAccessToken();
                if(accessToken != null) //If Already Logged
                {
                    Intent intent =  new Intent (MainActivity.this, HomeActivity.class);
                    intent.putExtra(Common.IS_LOGIN, true);
                    startActivity(intent);
                    finish();

                }else{
                    setContentView(R.layout.activity_main);
                    ButterKnife.bind(MainActivity.this);
                }

            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();


        //        printKeyHash();
    }

    private void printKeyHash() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}