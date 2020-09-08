package cn.nlifew.dmzj.ui.about;

import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ThirdParty {
    private static final String TAG = "ThirdParty";

    public static Iterator open(InputStream is) {
        return new Iterator(is);
    }

    public String name;
    public String author;
    public String description;
    public String url;

    @NonNull // 愚蠢的设计
    @Override
    public String toString() {
        return "ThirdParty{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public static final class Iterator implements java.util.Iterator<ThirdParty> {

        private Iterator(InputStream is) {
            mXmlParser = Xml.newPullParser();

            try {
                mXmlParser.setInput(is, "UTF-8");
                mType = mXmlParser.getEventType();
                while (mType != XmlPullParser.END_DOCUMENT) {
                    if (mType == XmlPullParser.START_TAG &&
                            "third-party".equals(mXmlParser.getName())) {
                        break;
                    }
                    mType = mXmlParser.next();
                }
            } catch (XmlPullParserException|IOException e) {
                Log.e(TAG, "Iterator: ", e);
                mType = XmlPullParser.END_DOCUMENT;
            }
        }

        private int mType;
        private final XmlPullParser mXmlParser;

        private int readNext() {
            try {
                return mXmlParser.next();
            } catch (XmlPullParserException | IOException e) {
                Log.e(TAG, "readNext: ", e);
            }
            return XmlPullParser.END_DOCUMENT;
        }

        public boolean hasNext() {
            while (mType != XmlPullParser.END_DOCUMENT) {
                if (mType == XmlPullParser.START_TAG &&
                        "item".equals(mXmlParser.getName())) {
                    return true;
                }
                mType = readNext();
            }
            return false;
        }

        public ThirdParty next() {
            ThirdParty obj = new ThirdParty();
            obj.name = mXmlParser.getAttributeValue(null, "name");
            obj.author = mXmlParser.getAttributeValue(null, "author");
            obj.url = mXmlParser.getAttributeValue(null, "url");
            obj.description = mXmlParser.getAttributeValue(null, "description");

            mType = readNext();
            return obj;
        }
    }

}
