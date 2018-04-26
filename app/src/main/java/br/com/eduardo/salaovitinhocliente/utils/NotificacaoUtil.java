package br.com.eduardo.salaovitinhocliente.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import br.com.eduardo.salaovitinhocliente.R;

/**
 * Created by eduardo.vasconcelos on 30/10/2017.
 */

public class NotificacaoUtil {

    public static void geraNotificacaoSimples(Context context, Intent it, String titulo, String mensagem, int id) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        Notification.Builder notification = new Notification.Builder(context);
        notification.setDefaults(Notification.DEFAULT_ALL);
        notification.setContentTitle(titulo);
        notification.setContentText(mensagem);
        notification.setSmallIcon(R.mipmap.ic_notifications_active_black_24dp);
        //notification.setLargeIcon();
        notification.setAutoCancel(true); //Auto cancela a notificacao ao clicar nela.
        notification.setContentIntent(pendingIntent);

        manager.notify(id, notification.build());
    }

    public static void cancelaNotificacao(Context context, int id) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.cancel(id);
    }

    public static void cancelaTodasNotificacoes(Context context) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.cancelAll();
    }
}
