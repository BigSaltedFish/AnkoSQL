package io.ztc.ankosql;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PhotoBean {
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    private String uuid;

    private String url;

    private String base64;

    @Generated(hash = 1414844640)
    public PhotoBean(Long _id, @NotNull String uuid, String url, String base64) {
        this._id = _id;
        this.uuid = uuid;
        this.url = url;
        this.base64 = base64;
    }

    @Generated(hash = 487180461)
    public PhotoBean() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase64() {
        return this.base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
