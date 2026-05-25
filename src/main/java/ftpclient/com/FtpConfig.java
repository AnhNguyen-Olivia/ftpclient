package ftpclient.com;

/**
 * Constants class that holds constant values used throughout the FTP client application.
 * This includes default hostnames for anonymous and custom logins, as well as the standard FTP port number.
 * @author Anh_Nguyen
 */
public class FtpConfig {
    // FTP server hostnames,port, and buffer size
    public static final String ANONYMOUS_HOST = "ftp.gnu.org";
    public static final String CUSTOM_HOST = "ftp.dlptest.com";
    public static final int FTP_PORT = 21;
    public static final int BUFFER_SIZE = 8192;

    // Default directories for uploads and downloads
    public static final String DEFAULT_DOWNLOAD_DIRECTORY = getDefaultDownloadDir();
    public static final String DEFAULT_UPLOAD_DIRECTORY = getDefaultUploadDir();

    // Default credentials for anonymous and custom logins
    public static final String DEFAULT_ANONYMOUS_USERNAME = "anonymous";
    public static final String DEFAULT_ANONYMOUS_PASSWORD = "anonymous";

    public static final String DEFAULT_CUSTOM_USERNAME = "dlpuser";
    public static final String DEFAULT_CUSTOM_PASSWORD = "rNrKYTX9g7z3RgJRmxWuGHbeu";

    // Timeout settings in milliseconds
    public static final int CONNECT_TIMEOUT_MS = 15000;
    public static final int READ_TIMEOUT_MS = 15000;

    private static String getDefaultDownloadDir() {
        String homeDir = System.getProperty("user.home");
        return new java.io.File(homeDir, "FtpClient/Download").getAbsolutePath();
    }

    private static String getDefaultUploadDir() {
        String homeDir = System.getProperty("user.home");
        return new java.io.File(homeDir, "FtpClient/Upload").getAbsolutePath();
    }

    private FtpConfig(){/*Private constructor to prevent installation :)))*/}
}
