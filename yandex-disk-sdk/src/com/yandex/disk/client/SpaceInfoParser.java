package com.yandex.disk.client;

import org.apache.http.HttpEntity;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class SpaceInfoParser extends Parser {

    public static final String SERVER_ENCODING = "UTF-8";

    public SpaceInfo spaceInfo;
    private boolean isStatusOK;

    public SpaceInfoParser(HttpEntity entity)
            throws XmlPullParserException, IOException {
        super(entity.getContent(), SERVER_ENCODING);
    }

    @Override
    public void tagStart(String path) {
        if ("/multistatus/response".equals(path)) {
            spaceInfo = new SpaceInfo();
        } else if ("/multistatus/response/propstat".equals(path)) {
            isStatusOK = false;
        }
    }

    @Override
    public void tagEnd(String path, String text) {
        if ("/multistatus/response/propstat/status".equals(path)) {
            isStatusOK = "HTTP/1.1 200 OK".equals(text);
        } else if (isStatusOK) {
            if ("/multistatus/response/propstat/prop/quota-available-bytes".equals(path)) {
                spaceInfo.quotaAvailableBytes = parseLong(text);
            } else if ("/multistatus/response/propstat/prop/quota-used-bytes".equals(path)) {
                spaceInfo.quotaUsedBytes = parseLong(text);
            }
        }
    }

    @Override
    public void parse()
            throws IOException, XmlPullParserException {
        super.parse();
    }

    private static long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}
