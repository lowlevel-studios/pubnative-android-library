package net.pubnative.player;

import android.content.Context;
import android.os.AsyncTask;

import net.pubnative.player.processor.VASTMediaPicker;
import net.pubnative.player.processor.VASTProcessor;
import net.pubnative.player.util.DefaultMediaPicker;
import net.pubnative.player.model.VASTModel;
import net.pubnative.player.util.VASTLog;

public class VASTParser extends AsyncTask<String, Object, VASTModel> {

    private static final String TAG = VASTParser.class.getName();

    public static final int ERROR_NONE                   = 0;
    public static final int ERROR_XML_OPEN_OR_READ       = 1;
    public static final int ERROR_XML_PARSE              = 2;
    public static final int ERROR_POST_VALIDATION        = 3;
    public static final int ERROR_EXCEEDED_WRAPPER_LIMIT = 4;

    private Context  context     = null;
    private Listener listener    = null;
    private int      resultError = ERROR_NONE;

    public interface Listener {

        void onVASTParserError(int error);
        void onVASTParserFinished(VASTModel model);
    }

    public VASTParser(Context context) {

        this.context = context;
    }

    public VASTParser setListener(Listener listener) {

        VASTLog.v(TAG, "setListener");

        this.listener = listener;
        return this;
    }

    @Override
    protected VASTModel doInBackground(String... params) {

        VASTLog.v(TAG, "doInBackground");

        VASTModel result = null;
        this.resultError = ERROR_NONE;

        String vastXML = null;
        if (params.length > 0) {
            vastXML = params[0];
        }

        if (vastXML != null) {

            VASTMediaPicker mediaPicker = new DefaultMediaPicker(this.context);
            VASTProcessor processor = new VASTProcessor(mediaPicker);

            int error = processor.process(params[0]);

            if (error == ERROR_NONE) {

                result = processor.getModel();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(VASTModel result) {

        VASTLog.v(TAG, "onPostExecute");

        if(this.listener != null){

            if(result == null){

                this.listener.onVASTParserError(this.resultError);

            } else {

                this.listener.onVASTParserFinished(result);
            }
        }
    }
}
