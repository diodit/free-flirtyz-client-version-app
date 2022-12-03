package com.ritssupreme.phlurtyz002.api.models;

import java.util.List;

public class AllAdverts{
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String message;
    public String status;
    public List<Datum> data;

    public class Datum{
        public String id;
        public String file;
        public String image;
        public String description;
        public String name;
        public String url;
        public String updated_at;
        public String created_at;
        public Object deleted_at;
    }
}