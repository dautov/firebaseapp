package com.example.bilal.firebaseapp.adapters

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.bilal.firebaseapp.model.MetinMesaj
import com.example.bilal.firebaseapp.R
import com.example.bilal.firebaseapp.activity.DisplayActivity
import com.example.bilal.firebaseapp.activity.DownloadActivity

import com.google.firebase.auth.FirebaseAuth

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.img_mesaj_sag.view.*
import kotlinx.android.synthetic.main.img_mesaj_sol.view.*
import kotlinx.android.synthetic.main.pdf_mesaj_sag.view.*
import kotlinx.android.synthetic.main.pdf_mesaj_sol.view.*
import kotlinx.android.synthetic.main.text_mesaj_sag.view.*
import kotlinx.android.synthetic.main.text_mesaj_sol.view.*
import java.lang.IllegalArgumentException


class SohbetMesajRecyclerViewAdapter(activity: AppCompatActivity, tumMejlar : ArrayList<MetinMesaj>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //var mContext = contextcontext : Context
    var mTumMesajlar = tumMejlar
    var myActivity = activity


    companion object {
         val TXT = 1
         val IMG = 2
         val DOC = 3

        var myContext : Context? = null
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        var inflater = LayoutInflater.from(p0.context)
        myContext = p0.context

        Log.e("createviewtest",p1.toString())


        when(p1){
            1 -> return  MesajViewHolder(inflater.inflate(R.layout.text_mesaj_sag,p0,false))
            2 -> return  MesajViewHolder2(inflater.inflate(R.layout.text_mesaj_sol,p0,false))
            3 -> return  ImageViewHolder(inflater.inflate(R.layout.img_mesaj_sag,p0,false))
            4 -> return  ImageViewHolder2(inflater.inflate(R.layout.img_mesaj_sol,p0,false))
            5 -> return  PDFMesajViewHolder(inflater.inflate(R.layout.pdf_mesaj_sag,p0,false))
            6 -> return  PDFMesajViewHolder2(inflater.inflate(R.layout.pdf_mesaj_sol,p0,false))
             else -> throw IllegalArgumentException("Invalid View Type") as Throwable

        }

        //return object : RecyclerView.ViewHolder(View(mContext)){}

    }

    override fun getItemCount(): Int {
        Log.e("MesajCount Test",mTumMesajlar.size.toString())
        return mTumMesajlar.size

    }


    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        var viewtype = mTumMesajlar.get(p1)
        Log.e("bindviewTest",p0.toString())

        when(p0){
            is MesajViewHolder -> p0.setData(viewtype,p1)
            is MesajViewHolder2 -> p0.setData(viewtype,p1)
            is ImageViewHolder -> p0.setIMG(viewtype,p1)
            is ImageViewHolder2 -> p0.setIMG(viewtype,p1)
            is PDFMesajViewHolder -> p0.setPDF(viewtype,p1)
            is PDFMesajViewHolder2 -> p0.setPDF(viewtype,p1)
        }


    }


    override fun getItemViewType(position: Int): Int {
        var view = mTumMesajlar.get(position)
        Log.e("viewtypetest",view.type.toString())
        if ((view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == TXT)){
            return 1
        }else if (!(view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == TXT)){
            return 2
        }else if ((view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == IMG)){
            return 3
        }else if ((!view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == IMG)){
            return 4
        }else if ((view.kullanici_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) && (view.type == DOC)){
            return 5
        }else{
            return 6
        }

    }



    class MesajViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var layout = itemView as FrameLayout
        var mesaj = layout.tvMesajSag
        var isim = layout.tvAdi
        var zaman = layout.message_time_sag

        fun setData(oAnkiMesaj: MetinMesaj,p1: Int){


            mesaj.text = oAnkiMesaj.mesaj
            isim.text = oAnkiMesaj.adi
            zaman.text = oAnkiMesaj.zaman


        }


    }class MesajViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var layout = itemView as FrameLayout
        var mesaj = layout.tvMesajSol
        var isim = layout.tvAdiSol
        var zaman = layout.message_time_sol

        fun setData(oAnkiMesaj: MetinMesaj,p1: Int){

            mesaj.text = oAnkiMesaj.mesaj
            isim.text = oAnkiMesaj.adi
            zaman.text = oAnkiMesaj.zaman


        }


    }

   inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout = itemView as FrameLayout
        var profilResim = layout.imgResimMesajSag
        var zaman = layout.textView_message_time_sag



        fun setIMG(oAnkiMesaj : MetinMesaj, p1: Int){
            var path = oAnkiMesaj.mesaj
            if (path.isNullOrEmpty() or path.isNullOrBlank()){
                Picasso.get().load(R.drawable.ic_account_circle).into(profilResim)
            }else {
                Picasso.get().load(path).into(profilResim)
            }
            zaman.text = oAnkiMesaj.zaman


            profilResim.setOnLongClickListener {
                var items = arrayOf<CharSequence>(
                    "İndir"
                )

                var dialog = AlertDialog.Builder(myActivity)
                dialog.setTitle("Belge")
                dialog.setItems(items,object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (which ==0){

                            var intent = Intent(myActivity, DownloadActivity::class.java)
                            intent.putExtra("linkDownload",oAnkiMesaj.mesaj)
                            myActivity.startActivity(intent)

                        }

                    }

                })
                dialog.show()

                return@setOnLongClickListener true
            }



        }

    }


    inner class ImageViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout = itemView as FrameLayout
        var profilResim = layout.imgResimMesajSol
        var zaman = layout.textView_message_time_sol



        fun setIMG(oAnkiMesaj : MetinMesaj, p1: Int){
            var path = oAnkiMesaj.mesaj
            if (path.isNullOrEmpty() or path.isNullOrBlank()){
                Picasso.get().load(R.drawable.ic_account_circle).into(profilResim)
            }else {
                Picasso.get().load(path).into(profilResim)
            }
            zaman.text = oAnkiMesaj.zaman

            profilResim.setOnLongClickListener {
                var items = arrayOf<CharSequence>(
                    "İndir"
                )

                var dialog = AlertDialog.Builder(myActivity)
                dialog.setTitle("Belge")
                dialog.setItems(items,object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (which ==0){

                            var intent = Intent(myActivity, DownloadActivity::class.java)
                            intent.putExtra("linkDownload",oAnkiMesaj.mesaj)
                            myActivity.startActivity(intent)

                        }

                    }

                })
                dialog.show()

                return@setOnLongClickListener true
            }



        }

    }

     inner class PDFMesajViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var belge = itemView as FrameLayout
        var pdf = belge.tvPdfMesajSag
        var zaman = belge.pdf_message_time_sag

        fun setPDF(oAnkiMesaj: MetinMesaj,p1: Int){
            pdf.text = "PDF Belgesi " + oAnkiMesaj.belge_adi +".pdf"
            zaman.text = oAnkiMesaj.zaman

            Log.e("BelgeTestPdf",oAnkiMesaj.belge_adi)
            belge.setOnLongClickListener {




                    var items = arrayOf<CharSequence>(
                        "Görüntüle",
                        "İndir"
                    )

                    var dialog = AlertDialog.Builder(myActivity)
                    dialog.setTitle("Belge")
                    dialog.setItems(items,object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            if (which ==0){
                                var intent = Intent(myActivity, DisplayActivity::class.java)
                                intent.putExtra("link",oAnkiMesaj.mesaj)
                                myActivity.startActivity(intent)
                            }else{
                                var intent = Intent(myActivity, DownloadActivity::class.java)
                                intent.putExtra("linkDownload",oAnkiMesaj.mesaj)
                                myActivity.startActivity(intent)

                            }

                        }

                    })
                    dialog.show()


                return@setOnLongClickListener true

            }
        }

    }

    inner class PDFMesajViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView) {
        var belge2 = itemView as FrameLayout
        var pdf = belge2.tvPdfMesajSol
        var zaman = belge2.pdf_message_time_sol

        fun setPDF(oAnkiMesaj: MetinMesaj,p1: Int){
            pdf.text = "PDF"
            zaman.text = oAnkiMesaj.zaman

            Log.e("BelgeTestPdf1",oAnkiMesaj.belge_adi)
            belge2.setOnLongClickListener {
                var items = arrayOf<CharSequence>(
                    "Görüntüle",
                    "İndir"
                )

                var dialog = AlertDialog.Builder(myActivity)
                dialog.setTitle("Belge")
                dialog.setItems(items,object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (which ==0){
                            var intent = Intent(myActivity, DisplayActivity::class.java)
                            intent.putExtra("link",oAnkiMesaj.mesaj)
                            myActivity.startActivity(intent)
                        }else{
                            //downloadFile(oAnkiMesaj.mesaj)
                            var intent = Intent(myActivity, DownloadActivity::class.java)
                            intent.putExtra("linkDownload",oAnkiMesaj.mesaj)
                            myActivity.startActivity(intent)
                        }

                    }

                })
                dialog.show()


                return@setOnLongClickListener true
            }

        }

    }

    private fun downloadFile(mesaj: String?) {

        val url = mesaj

        val request = DownloadManager.Request(Uri.parse(url))

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or  DownloadManager.Request.NETWORK_WIFI)
        request.setTitle("Download")
        request.setDescription("The File is Downloading ...")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"${System.currentTimeMillis()}")



    }








}

