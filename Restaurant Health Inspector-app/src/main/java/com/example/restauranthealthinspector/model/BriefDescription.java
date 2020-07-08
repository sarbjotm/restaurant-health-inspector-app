/**
 * Returns brief descriptions to activities from string.xml
 */
package com.example.restauranthealthinspector.model;
import com.example.restauranthealthinspector.R;
import android.content.Context;

public class BriefDescription {
    private int code;
    private String description = "";

    public BriefDescription(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBriefDescription(Context context){
        switch(code){
            case 101:
                description = context.getString(R.string.DL_101);
                break;

            case 102:
                description = context.getString(R.string.DL_102);
                break;

            case 103:
                description = context.getString(R.string.DL_103);
                break;

            case 104:
                description = context.getString(R.string.DL_104);
                break;

            case 201:
                description = context.getString(R.string.DL_201);
                break;

            case 202:
                description = context.getString(R.string.DL_202);
                break;

            case 203:
                description = context.getString(R.string.DL_203);
                break;

            case 204:
                description = context.getString(R.string.DL_204);
                break;

            case 205:
                description = context.getString(R.string.DL_205);
                break;

            case 206:
                description = context.getString(R.string.DL_206);
                break;

            case 208:
                description = context.getString(R.string.DL_208);
                break;

            case 209:
                description = context.getString(R.string.DL_209);
                break;

            case 210:
                description = context.getString(R.string.DL_210);
                break;

            case 211:
                description = context.getString(R.string.DL_211);
                break;

            case 212:
                description = context.getString(R.string.DL_212);
                break;

            case 301:
                description = context.getString(R.string.DL_301);
                break;

            case 302:
                description = context.getString(R.string.DL_302);
                break;

            case 303:
                description = context.getString(R.string.DL_303);
                break;

            case 304:
                description = context.getString(R.string.DL_304);
                break;

            case 305:
                description = context.getString(R.string.DL_305);
                break;

            case 306:
                description = context.getString(R.string.DL_306);
                break;

            case 307:
                description = context.getString(R.string.DL_307);
                break;

            case 308:
                description = context.getString(R.string.DL_308);
                break;

            case 309:
                description = context.getString(R.string.DL_309);
                break;

            case 310:
                description = context.getString(R.string.DL_310);
                break;

            case 311:
                description = context.getString(R.string.DL_311);
                break;

            case 312:
                description = context.getString(R.string.DL_312);
                break;

            case 313:
                description = context.getString(R.string.DL_313);
                break;

            case 314:
                description = context.getString(R.string.DL_314);
                break;

            case 315:
                description = context.getString(R.string.DL_315);
                break;

            case 401:
                description = context.getString(R.string.DL_401);
                break;

            case 402:
                description = context.getString(R.string.DL_402);
                break;

            case 403:
                description = context.getString(R.string.DL_403);
                break;

            case 404:
                description = context.getString(R.string.DL_404);
                break;

            case 501:
                description = context.getString(R.string.DL_501);
                break;

            case 502:
                description = context.getString(R.string.DL_502);
                break;

            default:
                description = "";
                break;
        }

        return description;
    }
}