package treecompany.cityofideas.fragments.qrscanner


import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.webkit.URLUtil
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

import treecompany.cityofideas.R
import treecompany.cityofideas.activities.qrscanner.QrScannerActivity

class QrScannerFragment : Fragment() {

    private lateinit var svBarcode: SurfaceView
    private lateinit var tvBarcode: TextView
    private lateinit var copy_button: Button
    private lateinit var goTo_button: Button

    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource: CameraSource


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_qr_scanner, container, false)

        svBarcode = root_view.findViewById(R.id.qr_surface)
        tvBarcode = root_view.findViewById(R.id.qr_text)
        copy_button = root_view.findViewById(R.id.qr_copy_button)
        goTo_button = root_view.findViewById(R.id.qr_goTo_button)


        detector = BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build()
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcodes = detections?.detectedItems
                if (barcodes!!.size() > 0) {
                    tvBarcode.post {
                        tvBarcode.text = barcodes.valueAt(0).displayValue
                        addEventHandlers()
                    }
                }
            }
        })

        cameraSource = CameraSource.Builder(context, detector).setRequestedPreviewSize(1024, 768)
            .setRequestedFps(25f)
            .setAutoFocusEnabled(true)
            .build()

        svBarcode.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(p0: SurfaceHolder?) {}
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ContextCompat.checkSelfPermission(
                        context as Context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    cameraSource.start(holder)
                else ActivityCompat.requestPermissions(
                    context as QrScannerActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    123
                )
            }
        })
        if (tvBarcode.text.isEmpty()) {
            tvBarcode.setText("Scan een barcode om verder te gaan")
        }

        return root_view
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraSource.start(svBarcode.holder)
            else Toast.makeText(context, "Scanner zal niet werken zonder toestemming", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }

    fun addEventHandlers() {
        copy_button.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Kopier de url", tvBarcode.text.toString())
            clipboard.primaryClip = clip
            QrScannerActivity.preConfig.displayToast("Gekopieerd naar klembord")
        }

        goTo_button.setOnClickListener {
            QrScannerActivity.preConfig.displayToast(tvBarcode.text.toString())
            goToUrl(tvBarcode.text.trim().toString())
        }
    }

    private fun goToUrl(url: String) {
        var uriUrl: Uri? = null
        if (URLUtil.isValidUrl(url)) {
            uriUrl = Uri.parse(url)
            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
            startActivity(launchBrowser)
        } else {
            QrScannerActivity.preConfig.displayToast("De url klopt niet")
        }
    }

}
