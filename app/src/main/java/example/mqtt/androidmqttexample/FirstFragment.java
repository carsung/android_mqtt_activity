package example.mqtt.androidmqttexample;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import androidx.navigation.fragment.NavHostFragment;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import example.mqtt.androidmqttexample.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {
    private final static String TAG = "GBSA_MQTT";
    private final static String SERVER_URI = "tcp://broker.hivemq.com";/*"tcp://iot.amtel.co.kr"*/;
    private final static String CLIENT_ID = "GBSA_TEST";
    private final static String TOPIC = "gbsa/5g";//"v1/devices/me/telemetry";
    private final static String Message = "{\"wifi_state\":\"1\"}";
    MqttAndroidClient mqttAndroidClient;
    TextView tv;

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MqttMessage message = new MqttMessage();
                message.setPayload(Message.getBytes());

                try {
                    mqttAndroidClient.publish(TOPIC, message);
                    tv.setText("message published : " + message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tv = getActivity().findViewById(R.id.textview_first);
        mqttAndroidClient = new MqttAndroidClient(getContext(), SERVER_URI, CLIENT_ID);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("gbsa_test_01");
        try {
            mqttAndroidClient.connect(options);
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.i(TAG, "connect to : " + serverURI);
                tv.setText("connect to : " + serverURI);

                try {
                    mqttAndroidClient.subscribe(TOPIC, 0, new IMqttMessageListener() {
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.i(TAG, "subscribed msg : " + message);
//                            tv.setText(new String(message.getPayload()));
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}