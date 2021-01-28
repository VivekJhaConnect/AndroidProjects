package com.example.print.document.demo;

public class WifiAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<ScanResult> mWifiList = new ArrayList<ScanResult>();

    public WifiAdapter(Activity mActivity, List<ScanResult> mWifiList) {
        this.mActivity = mActivity;
        this.mWifiList = mWifiList;
    }

    @Override
    public int getCount() {
        return mWifiList.size();
    }

    @Override
    public Object getItem(int i) {
        return mWifiList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_wifi_list_item, null);

        TextView txtWifiName = (TextView) view.findViewById(R.id.txtWifiName);
        txtWifiName.setText(mWifiList.get(i).SSID);

        return view;
    }

    public void setElements(List<ScanResult> mWifis) {
        this.mWifiList = mWifis;
        notifyDataSetChanged();
    }
}