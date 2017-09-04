package com.tvd.visitortracking.goojprt;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

public class Canvas {
    private static final String TAG = "Canvas";
    public IO IO = new IO();
    public static final int DIRECTION_LEFT_TO_RIGHT = 0;
    public static final int DIRECTION_BOTTOM_TO_TOP = 1;
    public static final int DIRECTION_RIGHT_TO_LEFT = 2;
    public static final int DIRECTION_TOP_TO_BOTTOM = 3;
    public static final int HORIZONTALALIGNMENT_LEFT = -1;
    public static final int HORIZONTALALIGNMENT_CENTER = -2;
    public static final int HORIZONTALALIGNMENT_RIGHT = -3;
    public static final int VERTICALALIGNMENT_TOP = -1;
    public static final int VERTICALALIGNMENT_CENTER = -2;
    public static final int VERTICALALIGNMENT_BOTTOM = -3;
    public static final int FONTSTYLE_BOLD = 8;
    public static final int FONTSTYLE_UNDERLINE = 128;
    public static final int BARCODE_TYPE_CODE128 = 73;
    private Bitmap bitmap;
    private android.graphics.Canvas canvas;
    private Paint paint;
    private int dir;

    public Canvas() {
    }

    public void Set(IO io) {
        if(io != null) {
            this.IO = io;
        }
    }

    public IO GetIO() {
        return this.IO;
    }

    public void CanvasBegin(int width, int height) {
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.canvas = new android.graphics.Canvas(this.bitmap);
        this.paint = new Paint();
        this.dir = 0;
        this.paint.setColor(-1);
        this.canvas.drawRect(0.0F, 0.0F, (float)width, (float)height, this.paint);
        this.paint.setColor(-16777216);
    }

    public void CanvasEnd() {
        this.paint = null;
        this.canvas = null;
    }

    public void CanvasPrint(int nBinaryAlgorithm, int nCompressMethod) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                Bitmap ex = this.bitmap;
                int nWidth = this.bitmap.getWidth();
                int dstw = (nWidth + 7) / 8 * 8;
                int dsth = ex.getHeight() * dstw / ex.getWidth();
                int[] dst = new int[dstw * dsth];
                ex = ImageProcessing.resizeImage(ex, dstw, dsth);
                ex.getPixels(dst, 0, dstw, 0, 0, dstw, dsth);
                byte[] gray = ImageProcessing.GrayImage(dst);
                boolean[] dithered = new boolean[dstw * dsth];
                if(nBinaryAlgorithm == 0) {
                    ImageProcessing.format_K_dither16x16(dstw, dsth, gray, dithered);
                } else {
                    ImageProcessing.format_K_threshold(dstw, dsth, gray, dithered);
                }

                Object data = null;
                byte[] data1;
                if(nCompressMethod == 0) {
                    data1 = ImageProcessing.eachLinePixToCmd(dithered, dstw, 0);
                } else {
                    data1 = ImageProcessing.eachLinePixToCompressCmd(dithered, dstw);
                }

                this.IO.Write(data1, 0, data1.length);
            } catch (Exception var14) {
                Log.i("Canvas", var14.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void SetPrintDirection(int direction) {
        this.dir = direction;
    }

    private float degreeTo360(float degree) {
        if((double)degree < 0.0D) {
            do {
                degree = (float)((double)degree + 360.0D);
            } while((double)degree < 0.0D);
        } else if((double)degree >= 360.0D) {
            do {
                degree = (float)((double)degree - 360.0D);
            } while((double)degree >= 360.0D);
        }

        return degree;
    }

    private void measureTranslate(float w, float h, float x, float y, float tw, float th, Canvas.DXDY dxdy, float rotation) {
        float dx = x;
        float dy = y;
        float abssinth = (float)Math.abs((double)th * Math.sin(0.017453292519943295D * (double)rotation));
        float abscosth = (float)Math.abs((double)th * Math.cos(0.017453292519943295D * (double)rotation));
        float abssintw = (float)Math.abs((double)tw * Math.sin(0.017453292519943295D * (double)rotation));
        float abscostw = (float)Math.abs((double)tw * Math.cos(0.017453292519943295D * (double)rotation));
        float dw = abssinth + abscostw;
        float dh = abscosth + abssintw;
        rotation = this.degreeTo360(rotation);
        if(rotation == 0.0F) {
            if(x == -1.0F) {
                dx = 0.0F;
            } else if(x == -2.0F) {
                dx = w / 2.0F - tw / 2.0F;
            } else if(x == -3.0F) {
                dx = w - tw;
            }

            if(y == -1.0F) {
                dy = 0.0F;
            } else if(y == -2.0F) {
                dy = h / 2.0F - th / 2.0F;
            } else if(y == -3.0F) {
                dy = h - th;
            }
        } else if(rotation > 0.0F && rotation < 90.0F) {
            if(x == -1.0F) {
                dx = abssinth;
            } else if(x == -2.0F) {
                dx = w / 2.0F - dw / 2.0F + abssinth;
            } else if(x == -3.0F) {
                dx = w - dw + abssinth;
            }

            if(y == -1.0F) {
                dy = 0.0F;
            } else if(y == -2.0F) {
                dy = h / 2.0F - dh / 2.0F;
            } else if(y == -3.0F) {
                dy = h - dh;
            }
        } else if(rotation == 90.0F) {
            if(x == -1.0F) {
                dx = th;
            } else if(x == -2.0F) {
                dx = w / 2.0F + th / 2.0F;
            } else if(x == -3.0F) {
                dx = w - y;
            }

            if(y == -1.0F) {
                dy = 0.0F;
            } else if(y == -2.0F) {
                dy = h / 2.0F - tw / 2.0F;
            } else if(y == -3.0F) {
                dy = h - tw;
            }
        } else if(rotation > 90.0F && rotation < 180.0F) {
            if(x == -1.0F) {
                dx = dw;
            } else if(x == -2.0F) {
                dx = w / 2.0F + dw / 2.0F;
            } else if(x == -3.0F) {
                dx = w - y;
            }

            if(y == -1.0F) {
                dy = abscosth;
            } else if(y == -2.0F) {
                dy = h / 2.0F - dh / 2.0F + abscosth;
            } else if(y == -3.0F) {
                dy = h - dh + abscosth;
            }
        } else if(rotation == 180.0F) {
            if(x == -1.0F) {
                dx = tw;
            } else if(x == -2.0F) {
                dx = w / 2.0F + tw / 2.0F;
            } else if(x == -3.0F) {
                dx = w;
            }

            if(y == -1.0F) {
                dy = th;
            } else if(y == -2.0F) {
                dy = h / 2.0F + th / 2.0F;
            } else if(y == -3.0F) {
                dy = h;
            }
        } else if(rotation > 180.0F && rotation < 270.0F) {
            if(x == -1.0F) {
                dx = dw - abscosth;
            } else if(x == -2.0F) {
                dx = w / 2.0F + dw / 2.0F - abscosth;
            } else if(x == -3.0F) {
                dx = w - abscosth;
            }

            if(y == -1.0F) {
                dy = dh;
            } else if(y == -2.0F) {
                dy = h / 2.0F + dh / 2.0F;
            } else if(y == -3.0F) {
                dy = h;
            }
        } else if(rotation == 270.0F) {
            if(x == -1.0F) {
                dx = 0.0F;
            } else if(x == -2.0F) {
                dx = (w - th) / 2.0F;
            } else if(x == -3.0F) {
                dx = w - th;
            }

            if(y == -1.0F) {
                dy = tw;
            } else if(y == -2.0F) {
                dy = (h + tw) / 2.0F;
            } else if(y == -3.0F) {
                dy = h;
            }
        } else if(rotation > 270.0F && rotation < 360.0F) {
            if(x == -1.0F) {
                dx = 0.0F;
            } else if(x == -2.0F) {
                dx = w / 2.0F - dw / 2.0F;
            } else if(x == -3.0F) {
                dx = w - dw;
            }

            if(y == -1.0F) {
                dy = dh - abscosth;
            } else if(y == -2.0F) {
                dy = h / 2.0F + dh / 2.0F - abscosth;
            } else if(y == -3.0F) {
                dy = h - abscosth;
            }
        }

        dxdy.dx = dx;
        dxdy.dy = dy;
    }

    public void DrawText(String text, float x, float y, float rotation, Typeface typeface, float textSize, int nFontStyle) {
        this.paint.setTypeface(typeface);
        this.paint.setTextSize(textSize);
        this.paint.setFakeBoldText((nFontStyle & 8) != 0);
        this.paint.setUnderlineText((nFontStyle & 128) != 0);
        float w = (float)this.canvas.getWidth();
        float h = (float)this.canvas.getHeight();
        Paint.FontMetricsInt fm = this.paint.getFontMetricsInt();
        float tw = this.paint.measureText(text);
        float th = (float)(fm.descent - fm.ascent);
        Canvas.DXDY dxdy = new Canvas.DXDY();
        this.canvas.save();
        if(this.dir == 0) {
            this.canvas.translate(0.0F, 0.0F);
        } else if(this.dir == 1) {
            this.canvas.translate(0.0F, h);
        } else if(this.dir == 2) {
            this.canvas.translate(w, h);
        } else if(this.dir == 3) {
            this.canvas.translate(w, 0.0F);
        }

        this.canvas.rotate((float)(this.dir * -90));
        if(this.dir != 0 && this.dir != 2) {
            this.measureTranslate(h, w, x, y, tw, th, dxdy, rotation);
        } else {
            this.measureTranslate(w, h, x, y, tw, th, dxdy, rotation);
        }

        this.canvas.translate(dxdy.dx, dxdy.dy);
        this.canvas.rotate(rotation);
        this.canvas.drawText(text, 0.0F, (float)(-fm.ascent), this.paint);
        this.canvas.restore();
    }

    public void DrawLine(float startX, float startY, float stopX, float stopY) {
        this.canvas.drawLine(startX, startY, stopX, stopY, this.paint);
    }

    public void DrawBox(float left, float top, float right, float bottom) {
        this.canvas.drawLine(left, top, right, top, this.paint);
        this.canvas.drawLine(right, top, right, bottom, this.paint);
        this.canvas.drawLine(right, bottom, left, bottom, this.paint);
        this.canvas.drawLine(left, bottom, left, top, this.paint);
    }

    public void DrawRect(float left, float top, float right, float bottom) {
        this.canvas.drawRect(left, top, right, bottom, this.paint);
    }

    public void DrawBitmap(Bitmap bitmap, float x, float y, float rotation) {
        float w = (float)this.canvas.getWidth();
        float h = (float)this.canvas.getHeight();
        float tw = (float)bitmap.getWidth();
        float th = (float)bitmap.getHeight();
        Canvas.DXDY dxdy = new Canvas.DXDY();
        this.canvas.save();
        if(this.dir == 0) {
            this.canvas.translate(0.0F, 0.0F);
        } else if(this.dir == 1) {
            this.canvas.translate(0.0F, h);
        } else if(this.dir == 2) {
            this.canvas.translate(w, h);
        } else if(this.dir == 3) {
            this.canvas.translate(w, 0.0F);
        }

        this.canvas.rotate((float)(this.dir * -90));
        if(this.dir != 0 && this.dir != 2) {
            this.measureTranslate(h, w, x, y, tw, th, dxdy, rotation);
        } else {
            this.measureTranslate(w, h, x, y, tw, th, dxdy, rotation);
        }

        this.canvas.translate(dxdy.dx, dxdy.dy);
        this.canvas.rotate(rotation);
        this.canvas.drawBitmap(bitmap, 0.0F, 0.0F, this.paint);
        this.canvas.restore();
    }

    /*public void DrawQRCode(String text, float x, float y, float rotation, int unitWidth, int version, int ecc) {
        Bitmap bitmap = null;
        int typeNumber = QRCode.getMinimumQRCodeTypeNumber(text, ecc - 1);
        if(version < typeNumber) {
            version = typeNumber;
        }

        QRCode codes = QRCode.getFixedSizeQRCode(text, ecc - 1, version);
        if(codes != null) {
            Boolean[][] bModules = codes.getModules();
            bitmap = this.QRModulesToBitmap(bModules, unitWidth);
        }

        this.DrawBitmap(bitmap, x, y, rotation);
    }*/

    private Bitmap QRModulesToBitmap(Boolean[][] modules, int unitWidth) {
        int height = modules.length * unitWidth;
        int width = height;
        int[] pixels = new int[height * height];
        Bitmap bitmap = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                pixels[width * y + x] = modules[y / unitWidth][x / unitWidth].booleanValue()?-16777216:-1;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /*public void DrawBarcode(String text, float x, float y, float rotation, int unitWidth, int height, int barcodeType) {
        Bitmap bitmap = null;
        if(barcodeType == 73) {
            DSCode128 code = new DSCode128();
            boolean[] bPattern = code.Encode(text);
            if(bPattern != null) {
                bitmap = this.BPatternToBitmap(bPattern, unitWidth, height);
            }
        }

        this.DrawBitmap(bitmap, x, y, rotation);
    }*/

    private Bitmap BPatternToBitmap(boolean[] bPattern, int unitWidth, int height) {
        int width = unitWidth * bPattern.length;
        int[] pixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                pixels[width * y + x] = bPattern[x / unitWidth]?-16777216:-1;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private class DXDY {
        float dx;
        float dy;

        private DXDY() {
        }
    }
}
