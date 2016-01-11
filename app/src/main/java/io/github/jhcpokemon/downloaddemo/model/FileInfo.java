package io.github.jhcpokemon.downloaddemo.model;


import java.io.Serializable;

public class FileInfo implements Serializable{
    private int id;
    private String name;
    private String url;
    private int length;
    private int finished;


    public FileInfo() {
    }

    public FileInfo(int id, String name, String url, int length, int finished) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getLength() {
        return length;
    }

    public int getFinished() {
        return finished;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }
}
