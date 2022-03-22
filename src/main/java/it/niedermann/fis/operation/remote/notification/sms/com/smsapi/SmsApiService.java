package it.niedermann.fis.operation.remote.notification.sms.com.smsapi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * @see <a href="https://www.smsapi.com/rest/">OpenAPI specification</a>
 */
interface SmsApiService {

    @GET("sms.do?format=json")
    Call<ResponseBody> sendSms(
            @Header("Authorization") String apiKey,
            @Query("from") String from,
            @Query("to") String to,
            @Query("message") String message,
            @Query("fast") Integer fast
    );
}
