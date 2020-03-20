package org.centerm.Tollway.utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.centerm.Tollway.R;

public class CustomDialog {

    private final String TAG = "CustomDialog";
    private Dialog dialog = null;
    private Context context = null;
    private EditText dataBox = null;
    private Button saveBtn;
    private Button cancelBtn;

    public CustomDialog(Context context, int ridLayout) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(ridLayout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setCancelable(boolean isCancelable) {
        dialog.setCancelable(isCancelable);
    }

    public void setInitWidgetDialog(String txt) {
        dataBox = dialog.findViewById(R.id.dataBox);
        saveBtn = dialog.findViewById(R.id.saveBtn);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        dataBox.setText(txt);
    }

    public void setMaxLength(int maxLength) {
        dataBox.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    }

    public void setInputText(int type) {
        dataBox.setInputType(type);
    }

    public void setInputFilter() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        dataBox.setFilters(filters);
    }

    public void setOnClickListener(final OnClickDialog onClickDialog){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickDialog != null) {
                    onClickDialog.onClickSave(dialog,dataBox.getText().toString());
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickDialog != null) {
                    onClickDialog.onClickCancel(dialog);
                }
            }
        });
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface OnClickDialog {
        void onClickSave(Dialog dialog, String sEt);
        void onClickCancel(Dialog dialog);
    }
}
