/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
import com.piece_framework.makegood.ui.views.ResultView;
import com.piece_framework.makegood.ui.views.ViewOpener;

/**
 * @since 1.3.0
  */
public class ResultSquare extends WorkbenchWindowControlContribution implements VisualResult {
    private static final String IMAGE_PATH_MAKEGOOD = "icons/MakeGood.gif"; //$NON-NLS-1$
    private static final String IMAGE_PATH_PASSED = "icons/square-passed.gif"; //$NON-NLS-1$
    private static final String IMAGE_PATH_FAILED = "icons/square-failed.gif"; //$NON-NLS-1$
    private static final String IMAGE_PATH_STOPPED = "icons/square-stopped.gif"; //$NON-NLS-1$
    private static final String IMAGE_PATH_PROGRESS = "icons/square-progress.gif"; //$NON-NLS-1$
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

        if (canvas != null && !canvas.isDisposed()) {
            canvas.dispose();
        }
        canvas = new Canvas(parent, SWT.NONE);
        canvas.setLayout(new GridLayout(1, false));
        square = new Label(canvas, SWT.NONE);
        square.setToolTipText("Wating for a Test Run"); //$NON-NLS-1$
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_MAKEGOOD).createImage());
        square.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                Job job = new UIJob("MakeGood View Open") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        ViewOpener.show(ResultView.VIEW_ID);
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

    @Override
    public void startTest() {
        square.setVisible(false);
        canvas.addPaintListener(imageAnimator.getPaintListener());
        imageAnimatorThread = new Thread(imageAnimator);
        imageAnimatorThread.start();
    }

    @Override
    public void endTest() {
        canvas.removePaintListener(imageAnimator.getPaintListener());
        imageAnimatorThread.interrupt();
        try {
            imageAnimatorThread.join();
        } catch (InterruptedException e) {
        }
        square.setVisible(true);
    }

    @Override
    public void markAsPassed() {
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_PASSED).createImage());
    }

    @Override
    public void markAsFailed() {
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_FAILED).createImage());
    }

    @Override
    public void markAsStopped() {
        square.setImage(Activator.getImageDescriptor(IMAGE_PATH_STOPPED).createImage());
    }

    private class ImageAnimator implements Runnable {
        private ImageLoader imageLoader = new ImageLoader();
        private Image image;
        private GC gc;
        private PaintListener paintListener;
        private int currentFrameIndex = 1;

        public ImageAnimator(InputStream imageInputStream) {
            super();
            imageLoader.load(imageInputStream);
            image = new Image(square.getDisplay(), imageLoader.data[0]);
            gc = new GC(image);
            paintListener = new PaintListener() {
                @Override
                public void paintControl(PaintEvent event) {
                    event.gc.drawImage(image, square.getBounds().x, square.getBounds().y);
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

                canvas.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run(){
                        ImageData nextFrameData = imageLoader.data[currentFrameIndex];
                        Image frameImage = new Image(canvas.getDisplay(), nextFrameData);
                        gc.drawImage(frameImage, nextFrameData.x, nextFrameData.y);
                        frameImage.dispose();
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
    }
}
