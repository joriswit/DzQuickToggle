package nl.joriswit.dzquicktoggle;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Patterns;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class DzServerUrl {

    private URL url;

    public DzServerUrl(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = defaultSharedPreferences.getString("server_url", null);
        if (serverUrl != null) {
            if (!serverUrl.contains("://")) {
                serverUrl = "http://" + serverUrl;
            }
            try {
                url = new URL(serverUrl);
            } catch (MalformedURLException e) {
                url = null;
            }
        } else {
            url = null;
        }
    }

    public URL getUrl() {
        return url;
    }

    public boolean isLocalNetworkUrl() {
        if (url == null) return false;
        String host = url.getHost();
        if (host == null) return false;

        if (!host.contains(".")
                || host.endsWith(".local")
                || host.endsWith(".lan")
                || host.endsWith(".home")) {
            return true;
        }

        if (host.startsWith("[fe80:") || host.equals("[::1]")) {
            return true;
        }

        if (Patterns.IP_ADDRESS.matcher(host).matches()) {
            InetAddress address;
            try {
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            return isLocalAddress(address);
        }
        return false;
    }

    private static boolean isLocalAddress(InetAddress addr) {
        return addr.isSiteLocalAddress()     // 10.x.x.x, 192.168.x.x, 172.16–31.x.x
                || addr.isLinkLocalAddress() // 169.254.x.x or fe80::/10
                || addr.isLoopbackAddress()  // 127.x.x.x or ::1
                || addr.isAnyLocalAddress(); // 0.0.0.0
    }
}
