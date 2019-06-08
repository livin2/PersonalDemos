package com.dhu777.hciapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings({"WeakerAccess", "unused"})
public class MyRecogResult{
    private static final int ERROR_NONE = 0;

    private String origalJson;
    private String[] resultsRecognition;
    private String origalResult;
    private String sn;
    private String desc;
    private String resultType;
    private String bestResult;
    private int error = -1;
    private int subError = -1;

    public static MyRecogResult parseJson(String jsonStr) {
        MyRecogResult result = new MyRecogResult();
        result.setOrigalJson(jsonStr);
        try {
            JSONObject json = new JSONObject(jsonStr);
            int error = json.optInt("error");
            int subError = json.optInt("sub_error");
            result.setError(error);
            result.setDesc(json.optString("desc"));
            result.setResultType(json.optString("result_type"));
            result.setSubError(subError);
            if (error == ERROR_NONE) {
                result.setOrigalResult(json.getString("origin_result"));
                result.setBestResult(json.getString("best_result"));
                JSONArray arr = json.optJSONArray("results_recognition");
                if (arr != null) {
                    int size = arr.length();
                    String[] recogs = new String[size];
                    for (int i = 0; i < size; i++) {
                        recogs[i] = arr.getString(i);
                    }
                    result.setResultsRecognition(recogs);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getBestResult() {
        return bestResult;
    }

    public void setBestResult(String bestResult) {
        this.bestResult = bestResult;
    }

    public boolean hasError() {
        return error != ERROR_NONE;
    }

    public boolean isFinalResult() {
        return "final_result".equals(resultType);
    }


    public boolean isPartialResult() {
        return "partial_result".equals(resultType);
    }

    public boolean isNluResult() {
        return "nlu_result".equals(resultType);
    }

    public String getOrigalJson() {
        return origalJson;
    }

    public void setOrigalJson(String origalJson) {
        this.origalJson = origalJson;
    }

    public String[] getResultsRecognition() {
        return resultsRecognition;
    }

    public void setResultsRecognition(String[] resultsRecognition) {
        this.resultsRecognition = resultsRecognition;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOrigalResult() {
        return origalResult;
    }

    public void setOrigalResult(String origalResult) {
        this.origalResult = origalResult;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public int getSubError() {
        return subError;
    }

    public void setSubError(int subError) {
        this.subError = subError;
    }
}
