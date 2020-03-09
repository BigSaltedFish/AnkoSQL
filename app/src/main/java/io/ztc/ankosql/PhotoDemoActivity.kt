package io.ztc.ankosql

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.shufeng.greendao.gen.PhotoBeanDaoUtils
import io.ztc.ankosql.tools.FileUtils
import io.ztc.appkit.base.AppActivity
import io.ztc.appkit.tools.ImgUtils
import kotlinx.android.synthetic.main.activity_photo_demo.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class PhotoDemoActivity : AppActivity(), EasyPermissions.PermissionCallbacks {
    //权限集合
    private val permissions = arrayOf(
        Manifest.permission.CAMERA
        , Manifest.permission.WRITE_EXTERNAL_STORAGE
        , Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var cameraSavePath: File? = null//拍照照片路径
    var uri: Uri? = null //拍照URI
    var dbUtils:PhotoBeanDaoUtils? = null

    override fun initContext(): Any {
        return this
    }

    override fun initDate(savedInstanceState: Bundle?) {
        dbUtils = PhotoBeanDaoUtils(context)
        cameraSavePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path + "/" + System.currentTimeMillis() + ".jpg")
    }

    override fun layout(): Int {
        return R.layout.activity_photo_demo
    }

    override fun initListener() {
        show.setOnClickListener {
            val sql = "where uuid = ?"
            val list = arrayOf("${show.text}")
            val back = dbUtils!!.queryPhotoBeanByNativeSql(sql,list)
            val map64 = back[0].base64
            val bitmap = FileUtils.getBitmapByUrl(map64)
            Glide.with(img.context).load(bitmap).into(img)
        }
        go.setOnClickListener {
            goCamera()
        }
    }

    fun base64ToBitmap(base64Data: String?): Bitmap? {
        val bytes: ByteArray = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun defaultAction() {
        getPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Thread{
            val photoPath: String?
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                //地址
                photoPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cameraSavePath.toString()
                } else {
                    uri!!.encodedPath
                }
                //保存和读取设置
                if (photoPath != null) {
                    try {
                        //获取使用的bitmap
                        var bitmap = ImgUtils.getBitmapByPath(photoPath)
                        val degree: Int = ImgUtils.getBitmapDegree(photoPath)
                        // 把图片旋转为正的方向
                        bitmap = ImgUtils.rotateBitmapByDegree(bitmap, degree)
                        val zbitmap = compressImage(bitmap)
                        //照片转BASE64
                        //val map64: String = ImgUtils.bitmapToBase64(zbitmap)
                        val backUrl = FileUtils.saveBitmap(zbitmap)
                        val uuid = UUID.randomUUID().toString()
                        //插入数据库操作
                        dbUtils!!.insertPhotoBean(PhotoBean(null,uuid,null,backUrl))
                        runOnUiThread {
                            show.text = uuid
                        }



                    } catch (e: Exception) {
                        toastFail("获取照片失败")
                    }
                }
            }
        }.start()

    }

    fun compressImage(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 1000) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset() //重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差 ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos) //这里压缩options%，把压缩后的数据存放到baos中
            options -= 10 //每次都减少10
        }
        val isBm =
            ByteArrayInputStream(baos.toByteArray()) //把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }

    //激活相机操作
    @SuppressLint("ObsoleteSdkInt")
    private fun goCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, packageName, cameraSavePath!!)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(cameraSavePath)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, 1)
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "请同意相关权限，否则应用无法使用", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show()
    }
    /**权限框架*/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    /**获取权限*/
    private fun getPermission() {
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            //已经打开权限
            Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show()
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取相关设备权限 请在设置中打开", 1, *permissions)
        }
    }

}
