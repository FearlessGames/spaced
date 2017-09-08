package se.ardortech.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

public class PropertiesDialogHelper {
	private static Logger logger = LoggerFactory.getLogger(PropertiesDialogHelper.class);

	public static PropertiesGameSettings getAttributes(final PropertiesGameSettings settings) {
        // Always show the dialog in these examples.
        URL dialogImage = null;
        final String dflt = settings.getDefaultSettingsWidgetImage();
        if (dflt != null) {
            try {
                dialogImage = PropertiesDialogHelper.class.getResource(dflt);
            } catch (final Exception e) {
                logger.debug("Resource lookup of '" + dflt + "' failed.  Proceeding.");
            }
        }
        if (dialogImage == null) {
            logger.debug("No dialog image loaded");
        } else {
            logger.debug("Using dialog image '" + dialogImage + "'");
        }

        final URL dialogImageRef = dialogImage;
        final AtomicReference<PropertiesDialog> dialogRef = new AtomicReference<PropertiesDialog>();
        final Stack<Runnable> mainThreadTasks = new Stack<Runnable>();
        try {
            if (EventQueue.isDispatchThread()) {
                dialogRef.set(new PropertiesDialog(settings, dialogImageRef, mainThreadTasks));
            } else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialogRef.set(new PropertiesDialog(settings, dialogImageRef, mainThreadTasks));
                    }
                });
            }
        } catch (final Exception e) {
            logger.debug("ExampleBase.getAttributes(settings)", e);
            return null;
        }

        PropertiesDialog dialogCheck = dialogRef.get();
        while (dialogCheck == null || dialogCheck.isVisible()) {
            try {
                // check worker queue for work
                while (!mainThreadTasks.isEmpty()) {
                    mainThreadTasks.pop().run();
                }
                // go back to sleep for a while
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                logger.warn("Error waiting for dialog system, using defaults.");
            }

            dialogCheck = dialogRef.get();
        }

        if (dialogCheck.isCancelled()) {
            System.exit(0);
        }
        return settings;
    }
}
