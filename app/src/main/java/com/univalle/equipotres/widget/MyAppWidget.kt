package com.univalle.equipotres.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.equipotres.R
import com.univalle.equipotres.di.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors

import com.univalle.equipotres.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

import com.univalle.equipotres.utils.SessionManager


class MyAppWidget : AppWidgetProvider() {

    companion object {

        private const val REQ_GEAR = 1001
        private const val REQ_VERIFY = 1002
        //private const val REQ_TOGGLE = 1003

        private const val ACTION_TOGGLE_BALANCE = "com.univalle.equipotres.widget.TOGGLE_BALANCE"
        private lateinit var sessionManager: SessionManager

        // Estado del botón de mostrar/ocultar saldo
        private var isBalanceVisible = false

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.app_widget)

            //-----verificar inicio de sesión-------
            //sessionManager = SessionManager(context)
            //-obtener las dependencias (hilt)
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java
            )

            val sessionManager = entryPoint.sessionManager()
            val firestore = entryPoint.firestore()

            //--------------------------------------------





            // Intent para abrir la app (botón engranaje)
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, REQ_GEAR, intent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btnGearWidget, pendingIntent)

            // Intent para alternar visibilidad del saldo (botón ojo)
            val toggleIntent = Intent(context, MyAppWidget::class.java).apply {
                action = ACTION_TOGGLE_BALANCE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btnEyeWidget, togglePendingIntent)




            if (sessionManager.isLoggedIn()) {

                if (isBalanceVisible) {

                    //ya no es necesario el uso manual
                    //val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                    firestore.collection("products")
                        .get()
                        .addOnSuccessListener { result ->

                            var totalBalance = 0.0
                            for (doc in result) {
                                val price = doc.getDouble("price") ?: 0.0
                                val quantity = doc.getLong("quantity")?.toInt() ?: 0
                                totalBalance += price * quantity
                            }

                            val formatoColombiano = NumberFormat.getNumberInstance(Locale("es", "CO"))
                            formatoColombiano.minimumFractionDigits = 2
                            formatoColombiano.maximumFractionDigits = 2
                            val saldoFormateado = "$ ${formatoColombiano.format(totalBalance)}"

                            views.setTextViewText(R.id.tvSaldoTotal, saldoFormateado)
                            views.setImageViewResource(R.id.btnEyeWidget, R.drawable.eye_slash)
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }
                        .addOnFailureListener {
                            views.setTextViewText(R.id.tvSaldoTotal, "Error")
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }

                } else {
                    views.setTextViewText(R.id.tvSaldoTotal, "$ * * * *")
                    views.setImageViewResource(R.id.btnEyeWidget, R.drawable.eye)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }

            } else {

                // Intent para abrir la app (botón eye) al no haber iniciado sesión
                val intentVerify = Intent(context, MainActivity::class.java).apply {
                    putExtra("opened_from_widget", true) //--con esto puedo verificar en el main si el open es desde el widget
                }
                SessionManager(context).setOpenedFromWidget(true)

                val pendingIntentVerify = PendingIntent.getActivity(
                    context, REQ_VERIFY, intentVerify, PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.btnEyeWidget, pendingIntentVerify)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }


        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_BALANCE) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1) {
                isBalanceVisible = !isBalanceVisible
                val appWidgetManager = AppWidgetManager.getInstance(context)
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}
