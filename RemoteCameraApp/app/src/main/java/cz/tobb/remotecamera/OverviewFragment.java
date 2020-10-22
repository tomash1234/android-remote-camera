package cz.tobb.remotecamera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OverviewFragment extends Fragment {

    private TextView tvRotation;
    private TextView tvAddress;
    private String localAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, null);

        tvRotation = view.findViewById(R.id.tv_rotation);
        tvAddress = view.findViewById(R.id.tv_address);

        localAddress = Utils.getIPAddress(true);
        tvAddress.setText(localAddress + ":" + CommunicationManager.PORT);
        return view;
    }

    public void updateRotation(float x, float y, float z){
        double xDeg = Math.toDegrees(x);
        double yDeg = Math.toDegrees(y);
        double zDeg = Math.toDegrees(z);
        String formattedText = String.format("x=%.2f° y=%.2f° z=%.2f°", xDeg, yDeg, zDeg);
        tvRotation.setText(formattedText);
    }
}
