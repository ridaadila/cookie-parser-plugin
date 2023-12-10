/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ridaadila.cookieparser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 *
 * @author ridaa
 */
public class CookieParser {
    private String file_name;
    private RandomAccessFile cookie_file;
    private int offset;

    public CookieParser(String file_name) {
        this.file_name = file_name;
        this.cookie_file = null;
        this.offset = 0;
    }

    public void open_file() {
        try {
            this.cookie_file = new RandomAccessFile(this.file_name, "r");
        } catch (IOException e) {
            System.out.println("Failed to open the cookie file: " + this.file_name);
        }
    }

    public void close_file() {
        try {
            if (this.cookie_file != null) {
                this.cookie_file.close();
            }
        } catch (IOException e) {
            System.out.println("Failed to close the cookie file: " + this.file_name);
        }
    }

    public List<Map<String, String>> read_cookie_file() {
        if (this.cookie_file == null) {
            System.out.println("No file opened.");
            return null;
        }
        try {
            byte[] file_header = this.read_chunk(4);
            if (!Arrays.equals(file_header, "cook".getBytes())) {
                System.out.println(this.file_name + " is not a binary cookie file.");
                return null;
            }
            int num_pages = this.read_chunk_big_endian(4);
            List<Integer> page_sizes = this.read_page_sizes(num_pages);
            List<Map<String, String>> cookies = this.read_cookies(page_sizes);
            return cookies;
        } catch (IOException e) {
            System.out.println("Failed to read the cookie file: " + this.file_name);
            return null;
        }
    }

    private void increment_offset(int chunk_size) {
        this.offset += chunk_size;
    }

    private byte[] read_chunk(int chunk_size) throws IOException {
        this.cookie_file.seek(this.offset);
        byte[] chunk = new byte[chunk_size];
        this.cookie_file.read(chunk);
        this.increment_offset(chunk_size);
        return chunk;
    }

    private int read_chunk_big_endian(int chunk_size) throws IOException {
        this.cookie_file.seek(this.offset);
        byte[] chunk = new byte[chunk_size];
        this.cookie_file.read(chunk);
        this.increment_offset(chunk_size);
        return ByteBuffer.wrap(chunk).getInt();
    }

    private int read_chunk_little_endian(int chunk_size) throws IOException {
        this.cookie_file.seek(this.offset);
        byte[] chunk = new byte[chunk_size];
        this.cookie_file.read(chunk);
        this.increment_offset(chunk_size);
        return ByteBuffer.wrap(chunk).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private double read_chunk_double(int chunk_size) throws IOException {
        this.cookie_file.seek(this.offset);
        byte[] chunk = new byte[chunk_size];
        this.cookie_file.read(chunk);
        this.increment_offset(chunk_size);
        return ByteBuffer.wrap(chunk).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    private List<Integer> read_page_sizes(int num_pages) throws IOException {
        List<Integer> page_sizes = new ArrayList<>();
        for (int page = 0; page < num_pages; page++) {
            int page_size = this.read_chunk_big_endian(4);
            page_sizes.add(page_size);
        }
        return page_sizes;
    }

    private List<Map<String, String>> read_cookies(List<Integer> page_sizes) throws IOException {
        List<Map<String, String>> cookies = new ArrayList<>();
        for (int i = 0; i < page_sizes.size(); i++) {
            this.read_chunk(4);

            int cookie_number = this.read_chunk_little_endian(4);
            List<Integer> cookie_offsets = this.read_cookie_offsets(cookie_number);

            this.read_chunk(4);
            for (int offset : cookie_offsets) {
                Map<String, String> cookie = this.read_cookie(offset);
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    private List<Integer> read_cookie_offsets(int cookie_number) throws IOException {
        List<Integer> cookie_offsets = new ArrayList<>();
        for (int i = 0; i < cookie_number; i++) {
            int offset = this.read_chunk_little_endian(4);
            cookie_offsets.add(offset);
        }
        return cookie_offsets;
    }

    private Map<String, String> read_cookie(int offset) throws IOException {
        int cookie_size = this.read_chunk_little_endian(4);

        this.read_chunk(4);
        int flag = this.read_chunk_little_endian(4);
        String cookie_flag = this.get_cookie_flag(flag);

        this.read_chunk(4);
        int urloffset = this.read_chunk_little_endian(4);
        int nameoffset = this.read_chunk_little_endian(4);
        int pathoffset = this.read_chunk_little_endian(4);
        int valueoffset = this.read_chunk_little_endian(4);

        this.read_chunk(8);
        double expiry_date_epoch = this.read_chunk_double(8) + 978307200;
        String expiry_date = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").format(new Date((long) expiry_date_epoch * 1000));
        double create_date_epoch = this.read_chunk_double(8) + 978307200;
        String create_date = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").format(new Date((long) create_date_epoch * 1000));
        String url = this.read_null_terminated_string();
        String name = this.read_null_terminated_string();
        String path = this.read_null_terminated_string();
        String value = this.read_null_terminated_string();
        Map<String, String> cookie = new HashMap<>();
        cookie.put("name", name);
        cookie.put("value", value);
        cookie.put("url", url);
        cookie.put("path", path);
        cookie.put("expiry_date", expiry_date);
        cookie.put("create_date", create_date);
        cookie.put("cookie_flag", cookie_flag);
        return cookie;
    }

    private String read_null_terminated_string() throws IOException {
        StringBuilder string = new StringBuilder();
        byte[] char_bytes = new byte[1];
        this.cookie_file.seek(this.offset);
        this.cookie_file.read(char_bytes);
        this.increment_offset(1);
        while (char_bytes[0] != 0) {
            string.append(new String(char_bytes, "UTF-8"));
            this.cookie_file.seek(this.offset);
            this.cookie_file.read(char_bytes);
            this.increment_offset(1);
        }
        return string.toString();
    }

    private String get_cookie_flag(int flag) {
        if (flag == 0) {
            return "";
        } else if (flag == 1) {
            return "Secure";
        } else if (flag == 4) {
            return "HttpOnly";
        } else if (flag == 5) {
            return "Secure; HttpOnly";
        } else {
            return "Unknown";
        }
    }

    public String getStringInJsonFormatFromCookies(List<Map<String, String>> cookies)
    {
        int size_of_cookies = cookies.size();
        int counter_primary = 1;

        String json_final;

        if(size_of_cookies == 0)
        {
            json_final = "[]";
        }
        else {
            json_final = "[\n";
            for (Map<String, String> cookie : cookies) {
                String json = "  {\n";
                int size_of_cookie = cookie.size();
                int counter = 1;
                for (Map.Entry<String, String> entry : cookie.entrySet()) {
                    json += "   " + entry.getKey() + ": " + entry.getValue();

                    if(counter < size_of_cookie)
                    {
                        json += ",\n";
                    }
                    else {
                        json += "\n";
                    }
                    counter++;
                }

                if(counter_primary < size_of_cookies)
                {
                    json += "  },\n";
                }
                else {
                    json += "  }\n";
                }

                counter_primary ++;

                json_final += json;
            }
            json_final += "]";
        }

        return json_final;
    }
}