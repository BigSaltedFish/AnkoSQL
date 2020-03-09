package com.shufeng.greendao.gen;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import io.ztc.ankosql.PhotoBean;

public class PhotoBeanDaoUtils {
    private static final String TAG = PhotoBeanDaoUtils.class.getSimpleName();
    private DaoManager mManager;

    public PhotoBeanDaoUtils(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成meizi记录的插入，如果表未创建，先创建Meizi表
     * @param PhotoBean
     * @return
     */
    public boolean insertPhotoBean(PhotoBean PhotoBean){
        boolean flag = false;
        flag = mManager.getDaoSession().getPhotoBeanDao().insert(PhotoBean) != -1;
        Log.i(TAG, "insert PhotoBean :" + flag + "-->" + PhotoBean.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param PhotoBeanList
     * @return
     */
    public boolean insertMultPhotoBean(final List<PhotoBean> PhotoBeanList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (PhotoBean meizi : PhotoBeanList) {
                        mManager.getDaoSession().insertOrReplace(meizi);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     * @param PhotoBean
     * @return
     */
    public boolean updatePhotoBean(PhotoBean PhotoBean){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(PhotoBean);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param PhotoBean
     * @return
     */
    public boolean deletePhotoBean(PhotoBean PhotoBean){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(PhotoBean);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(PhotoBean.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<PhotoBean> queryAllPhotoBean(){
        return mManager.getDaoSession().loadAll(PhotoBean.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public PhotoBean queryPhotoBeanById(long key){
        return mManager.getDaoSession().load(PhotoBean.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<PhotoBean> queryPhotoBeanByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(PhotoBean.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<PhotoBean> queryPhotoBeanByQueryBuilder(long id){
        QueryBuilder<PhotoBean> queryBuilder = mManager.getDaoSession().queryBuilder(PhotoBean.class);
        return queryBuilder.where(PhotoBeanDao.Properties._id.eq(id)).list();
    }
}
