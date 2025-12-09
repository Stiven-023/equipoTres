package com.univalle.equipotres.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.equipotres.R
import com.univalle.equipotres.database.AppDatabase
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

            // Intent para abrir la app (botón engranaje)
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
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


            //-----verificar inicio de sesión-------
            sessionManager = SessionManager(context)

            if (sessionManager.isLoggedIn()){
                // Mostrar saldo oculto o visible según el estado
                if (isBalanceVisible) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(context)
                        val totalBalance = db.productDao().getTotalInventoryValue() ?: 0.0

                        val formatoColombiano = NumberFormat.getNumberInstance(Locale("es", "CO"))
                        formatoColombiano.minimumFractionDigits = 2
                        formatoColombiano.maximumFractionDigits = 2
                        val saldoFormateado = "$ ${formatoColombiano.format(totalBalance)}"

                        withContext(Dispatchers.Main) {
                            views.setTextViewText(R.id.tvSaldoTotal, saldoFormateado)
                            views.setImageViewResource(R.id.btnEyeWidget, R.drawable.eye_slash)
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }
                    }
                } else {
                    // Mostrar asteriscos
                    views.setTextViewText(R.id.tvSaldoTotal, "$ * * * *")
                    views.setImageViewResource(R.id.btnEyeWidget, R.drawable.eye)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }else{
                // Intent para abrir la app (botón eye) al no haber iniciado sesión
                val intentVerify = Intent(context, MainActivity::class.java)
                val pendingIntentVerify = PendingIntent.getActivity(
                    context, 0, intentVerify, PendingIntent.FLAG_IMMUTABLE
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
