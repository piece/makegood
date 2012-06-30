/**
 * Copyright (c) 2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.views.ResultView;
import com.piece_framework.makegood.ui.views.ViewOpener;

/**
 * @since 1.3.0
 */
public class ResultSquare extends WorkbenchWindowControlContribution {
    private static final String IMAGE_PATH_MAKEGOOD = "icons/makegood.gif"; //$NON-NLS-1$
    private static final String IMAGE_PATH_PASSED = "icons/square_passed.gif"; //$NON-NLS-1$
    private static final String IMAGE_PATH_FAILED = "icons/square_failed.gif"; //$NON-NLS-1$
    private static final String IMAGE_PATH_STOPPED = "icons/square_stopped.gif"; //$NON-NLS-1$

    /**
     * @since 2.1.0
     */
    private static final String IMAGE_PATH_NOTESTS = "icons/square_notests.gif"; //$NON-NLS-1$

    private static final String IMAGE_PATH_PROGRESS = "icons/square_progress.gif"; //$NON-NLS-1$
    private Canvas canvas;
    private Label square;
    private ImageAnimator imageAnimator;
    private Thread imageAnimatorThread;
    private static ResultSquare currentResultSquare;

    public static ResultSquare getInstance() {
        return currentResultSquare;
    }

    @Override
    protected Control createControl(Composite parent) {
        currentResultSquare = this;

        canvas = new Canvas(parent, SWT.NONE);
        canvas.setLayout(new GridLayout(1, false));
        square = new Label(canvas, SWT.NONE);
        square.setToolTipText(Messages.ResultSquare_WaitingForTestRun);
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_MAKEGOOD).createImage());
        square.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                Job job = new UIJob("MakeGood View Open") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        ViewOpener.open(ResultView.VIEW_ID);
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseUp(MouseEvent e) {
            }
        });

        URL imageURL = Activator.getDefault().getBundle().getEntry(IMAGE_PATH_PROGRESS);
        if (imageURL == null) {
            throw new RuntimeException(IMAGE_PATH_PROGRESS + " not found."); //$NON-NLS-1$
        }
        InputStream imageInputStream;
        try {
            imageInputStream = imageURL.openStream();
        } catch (IOException e) {
            throw new RuntimeException("Cannnot open stream for " + IMAGE_PATH_PROGRESS + "."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        imageAnimator = new ImageAnimator(imageInputStream);

        return canvas;
    }

    public void startTest() {
        square.setVisible(false);
        canvas.addPaintListener(imageAnimator.getPaintListener());
        imageAnimatorThread = new Thread(imageAnimator);
        imageAnimatorThread.start();
    }

    public void endTest() {
        if (imageAnimatorThread != null) {
            imageAnimatorThread.interrupt();
            try {
                imageAnimatorThread.join();
            } catch (InterruptedException e) {
            }
        }
        canvas.removePaintListener(imageAnimator.getPaintListener());
        square.setVisible(true);
    }

    public void markAsPassed() {
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_PASSED).createImage());
        square.setToolTipText(Messages.ResultSquare_TestPassed);
    }

    public void markAsFailed() {
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_FAILED).createImage());
        square.setToolTipText(Messages.ResultSquare_TestFailed);
    }

    public void markAsStopped() {
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_STOPPED).createImage());
        square.setToolTipText(Messages.ResultSquare_TestStopped);
    }

    /**
     * @since 2.1.0
     */
    public void markAsNoTests() {
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_NOTESTS).createImage());
        square.setToolTipText(Messages.ResultSquare_NoTests);
    }

    private class ImageAnimator implements Runnable {
        private ImageLoader imageLoader = new ImageLoader();
        private GC gc;
        private PaintListener paintListener;
        private int currentFrameIndex = 1;

        public ImageAnimator(InputStream imageInputStream) {
            super();
            imageLoader.load(imageInputStream);
            gc = new GC(canvas);
            paintListener = new PaintListener() {
                @Override
                public void paintControl(PaintEvent event) {
                    drawCurrentFrame(event.gc);
                }
            };
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(imageLoader.data[currentFrameIndex].delayTime * 10);
                } catch (InterruptedException e) {
                    return;
                }

                if (canvas.isDisposed()) return;
                canvas.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run(){
                        if (canvas.isDisposed()) return;
                        drawCurrentFrame(gc);
                        if (canvas.isDisposed()) return;
                        canvas.redraw();

                        if (currentFrameIndex == imageLoader.data.length - 1) {
                            currentFrameIndex = 0;
                        } else {
                            ++currentFrameIndex;
                        }
                    }
                });
            }
        }

        public PaintListener getPaintListener() {
            return paintListener;
        }

        /**
         * @since 1.7.0
         */
        private void drawCurrentFrame(GC gc) {
            ImageData currentFrameData = imageLoader.data[currentFrameIndex];
            Image frameImage = new Image(canvas.getDisplay(), currentFrameData);
            gc.drawImage(frameImage, square.getBounds().x, square.getBounds().y);
            frameImage.dispose();
        }
    }
}
