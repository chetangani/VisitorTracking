package com.tvd.visitortracking.goojprt;

import android.graphics.Bitmap;
import android.util.Log;

public class Pos {
    private static final String TAG = "Pos";
    private IO IO = new IO();
    private ESCCmd Cmd = new ESCCmd();

    public Pos() {
    }

    public void Set(IO io) {
        if(io != null) {
            this.IO = io;
        }

    }

    public IO GetIO() {
        return this.IO;
    }

    public void POS_PrintPicture(Bitmap mBitmap, int nWidth, int nBinaryAlgorithm, int nCompressMethod) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();
            try {
                int ex = (nWidth + 7) / 8 * 8;
                int dsth = (mBitmap.getHeight()) * ex / mBitmap.getWidth();
                int[] dst = new int[ex * dsth];
                mBitmap = ImageProcessing.resizeImage(mBitmap, ex, dsth);
                mBitmap.getPixels(dst, 0, ex, 0, 0, ex, dsth);
                byte[] gray = ImageProcessing.GrayImage(dst);
                boolean[] dithered = new boolean[ex * dsth];
                if(nBinaryAlgorithm == 0) {
                    ImageProcessing.format_K_dither16x16(ex, dsth, gray, dithered);
                } else {
                    ImageProcessing.format_K_threshold(ex, dsth, gray, dithered);
                }
                Object data = null;
                byte[] data1;
                if(nCompressMethod == 0) {
                    data1 = ImageProcessing.eachLinePixToCmd(dithered, ex, 0);
                } else {
                    data1 = ImageProcessing.eachLinePixToCompressCmd(dithered, ex);
                }
                this.IO.Write(data1, 0, data1.length);
            } catch (Exception var14) {
                Log.i("Pos", var14.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_S_TextOut(String pszString, int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nOrgx > '\uffff' || nOrgx < 0 || nWidthTimes > 7 || nWidthTimes < 0 || nHeightTimes > 7 || nHeightTimes < 0 || nFontType < 0 || nFontType > 4 || pszString.length() == 0) {
                    throw new Exception("invalid args");
                }

                this.Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
                this.Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
                byte[] ex = new byte[]{0, 16, 32, 48, 64, 80, 96, 112};
                byte[] intToHeight = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
                this.Cmd.GS_exclamationmark_n[2] = (byte)(ex[nWidthTimes] + intToHeight[nHeightTimes]);
                byte[] tmp_ESC_M_n = this.Cmd.ESC_M_n;
                if(nFontType != 0 && nFontType != 1) {
                    tmp_ESC_M_n = new byte[0];
                } else {
                    tmp_ESC_M_n[2] = (byte)nFontType;
                }

                this.Cmd.GS_E_n[2] = (byte)(nFontStyle >> 3 & 1);
                this.Cmd.ESC_line_n[2] = (byte)(nFontStyle >> 7 & 3);
                this.Cmd.FS_line_n[2] = (byte)(nFontStyle >> 7 & 3);
                this.Cmd.ESC_lbracket_n[2] = (byte)(nFontStyle >> 9 & 1);
                this.Cmd.GS_B_n[2] = (byte)(nFontStyle >> 10 & 1);
                this.Cmd.ESC_V_n[2] = (byte)(nFontStyle >> 12 & 1);
                this.Cmd.ESC_9_n[2] = 1;
                byte[] pbString = pszString.getBytes();
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.ESC_dollors_nL_nH, this.Cmd.GS_exclamationmark_n, tmp_ESC_M_n, this.Cmd.GS_E_n, this.Cmd.ESC_line_n, this.Cmd.FS_line_n, this.Cmd.ESC_lbracket_n, this.Cmd.GS_B_n, this.Cmd.ESC_V_n, this.Cmd.FS_AND, this.Cmd.ESC_9_n, pbString});
                this.IO.Write(data, 0, data.length);
            } catch (Exception var15) {
                Log.i("Pos", var15.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_FeedLine() {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                byte[] ex = this.byteArraysToBytes(new byte[][]{this.Cmd.CR, this.Cmd.LF});
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var5) {
                Log.i("Pos", var5.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_S_Align(int align) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(align < 0 || align > 2) {
                    throw new Exception("invalid args");
                }

                byte[] ex = this.Cmd.ESC_a_n;
                ex[2] = (byte)align;
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_SetLineHeight(int nHeight) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nHeight < 0 || nHeight > 255) {
                    throw new Exception("invalid args");
                }

                byte[] ex = this.Cmd.ESC_3_n;
                ex[2] = (byte)nHeight;
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_S_SetBarcode(String strCodedata, int nOrgx, int nType, int nWidthX, int nHeight, int nHriFontType, int nHriFontPosition) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nOrgx < 0 || nOrgx > '\uffff' || nType < 65 || nType > 73 || nWidthX < 2 || nWidthX > 6 || nHeight < 1 || nHeight > 255) {
                    throw new Exception("invalid args");
                }

                byte[] ex = strCodedata.getBytes();
                this.Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
                this.Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
                this.Cmd.GS_w_n[2] = (byte)nWidthX;
                this.Cmd.GS_h_n[2] = (byte)nHeight;
                this.Cmd.GS_f_n[2] = (byte)(nHriFontType & 1);
                this.Cmd.GS_H_n[2] = (byte)(nHriFontPosition & 3);
                this.Cmd.GS_k_m_n_[2] = (byte)nType;
                this.Cmd.GS_k_m_n_[3] = (byte)ex.length;
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.ESC_dollors_nL_nH, this.Cmd.GS_w_n, this.Cmd.GS_h_n, this.Cmd.GS_f_n, this.Cmd.GS_H_n, this.Cmd.GS_k_m_n_, ex});
                this.IO.Write(data, 0, data.length);
            } catch (Exception var13) {
                Log.i("Pos", var13.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_S_SetQRcode(String strCodedata, int nWidthX, int nVersion, int nErrorCorrectionLevel) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();
            try {
                if(nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1 || nErrorCorrectionLevel > 4 || nVersion < 0 || nVersion > 16) {
                    throw new Exception("invalid args");
                }
                byte[] ex = strCodedata.getBytes();
                this.Cmd.GS_w_n[2] = (byte)nWidthX;
                this.Cmd.GS_k_m_v_r_nL_nH[3] = (byte)nVersion;
                this.Cmd.GS_k_m_v_r_nL_nH[4] = (byte)nErrorCorrectionLevel;
                this.Cmd.GS_k_m_v_r_nL_nH[5] = (byte)(ex.length & 255);
                this.Cmd.GS_k_m_v_r_nL_nH[6] = (byte)((ex.length & '\uff00') >> 8);
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.GS_w_n, this.Cmd.GS_k_m_v_r_nL_nH, ex});
                this.IO.Write(data, 0, data.length);
            } catch (Exception var10) {
                Log.i("Pos", var10.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_EPSON_SetQRCode(String strCodedata, int nWidthX, int nErrorCorrectionLevel) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1 || nErrorCorrectionLevel > 4) {
                    throw new Exception("invalid args");
                }

                byte[] ex = strCodedata.getBytes();
                this.Cmd.GS_leftbracket_k_pL_pH_cn_67_n[7] = (byte)nWidthX;
                this.Cmd.GS_leftbracket_k_pL_pH_cn_69_n[7] = (byte)(47 + nErrorCorrectionLevel);
                this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[3] = (byte)(ex.length + 3 & 255);
                this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[4] = (byte)((ex.length + 3 & '\uff00') >> 8);
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.GS_leftbracket_k_pL_pH_cn_67_n, this.Cmd.GS_leftbracket_k_pL_pH_cn_69_n, this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk, ex, this.Cmd.GS_leftbracket_k_pL_pH_cn_fn_m});
                this.IO.Write(data, 0, data.length);
            } catch (Exception var9) {
                Log.i("Pos", var9.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_Reset() {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                byte[] ex = this.Cmd.ESC_ALT;
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var5) {
                Log.i("Pos", var5.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_SetMotionUnit(int nHorizontalMU, int nVerticalMU) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nHorizontalMU < 0 || nHorizontalMU > 255 || nVerticalMU < 0 || nVerticalMU > 255) {
                    throw new Exception("invalid args");
                }

                byte[] ex = this.Cmd.GS_P_x_y;
                ex[2] = (byte)nHorizontalMU;
                ex[3] = (byte)nVerticalMU;
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    protected void POS_SetCharSetAndCodePage(int nCharSet, int nCodePage) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nCharSet < 0 || nCharSet > 15 || nCodePage < 0 || nCodePage > 19 || nCodePage > 10 && nCodePage < 16) {
                    throw new Exception("invalid args");
                }

                this.Cmd.ESC_R_n[2] = (byte)nCharSet;
                this.Cmd.ESC_t_n[2] = (byte)nCodePage;
                byte[] ex = this.byteArraysToBytes(new byte[][]{this.Cmd.ESC_R_n, this.Cmd.ESC_t_n});
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_SetRightSpacing(int nDistance) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nDistance < 0 || nDistance > 255) {
                    throw new Exception("invalid args");
                }

                this.Cmd.ESC_SP_n[2] = (byte)nDistance;
                byte[] ex = this.Cmd.ESC_SP_n;
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_S_SetAreaWidth(int nWidth) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                if(nWidth < 0 || nWidth > '\uffff') {
                    throw new Exception("invalid args");
                }

                byte ex = (byte)(nWidth % 256);
                byte nH = (byte)(nWidth / 256);
                this.Cmd.GS_W_nL_nH[2] = ex;
                this.Cmd.GS_W_nL_nH[3] = nH;
                byte[] data = this.Cmd.GS_W_nL_nH;
                this.IO.Write(data, 0, data.length);
            } catch (Exception var8) {
                Log.i("Pos", var8.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_CutPaper() {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                byte[] ex = new byte[]{29, 86, 66, 0};
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var5) {
                Log.i("Pos", var5.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_Beep(int nBeepCount, int nBeepMillis) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                byte[] ex = new byte[]{27, 66, (byte)nBeepCount, (byte)nBeepMillis};
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_KickDrawer(int nDrawerIndex, int nPulseTime) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                byte[] ex = new byte[]{27, 112, (byte)nDrawerIndex, (byte)nPulseTime, (byte)nPulseTime};
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public void POS_SetPrintSpeed(int nSpeed) {
        if(this.IO.IsOpened()) {
            this.IO.Lock();

            try {
                byte[] ex = new byte[]{31, 40, 115, 2, 0, (byte)((int)((long)nSpeed & 255L)), (byte)((int)(((long)nSpeed & 65280L) >> 8))};
                this.IO.Write(ex, 0, ex.length);
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.Unlock();
            }

        }
    }

    public boolean POS_QueryStatus(byte[] status, int timeout, int MaxRetry) {
        if(!this.IO.IsOpened()) {
            return false;
        } else {
            this.IO.Lock();
            boolean result = false;

            try {
                byte[] ex = new byte[]{29, 114, 1, 29, 114, 1, 29, 114, 1, 29, 114, 1};

                while(MaxRetry-- >= 0) {
                    this.IO.SkipAvailable();
                    if(this.IO.Write(ex, 0, ex.length) == ex.length && this.IO.Read(status, 0, 1, timeout) == 1) {
                        result = true;
                        break;
                    }
                }
            } catch (Exception var9) {
                Log.i("Pos", var9.toString());
            } finally {
                this.IO.Unlock();
            }

            return result;
        }
    }

    public boolean POS_RTQueryStatus(byte[] status, int type, int timeout, int MaxRetry) {
        if(!this.IO.IsOpened()) {
            return false;
        } else {
            this.IO.Lock();
            boolean result = false;

            try {
                byte[] cmd = new byte[]{16, 4, (byte)type, 16, 4, (byte)type, 16, 4, (byte)type, 16, 4, (byte)type};

                while(MaxRetry-- >= 0) {
                    this.IO.SkipAvailable();
                    if(this.IO.Write(cmd, 0, cmd.length) == cmd.length && this.IO.Read(status, 0, 1, timeout) == 1) {
                        result = true;
                        break;
                    }
                }
            } catch (Exception var10) {
                ;
            } finally {
                this.IO.Unlock();
            }

            return result;
        }
    }

    public boolean POS_TicketSucceed(int dwSendIndex, int timeout) {
        if(!this.IO.IsOpened()) {
            return false;
        } else {
            this.IO.Lock();
            boolean result = false;

            try {
                Log.i("Pos", String.format("Get Ticket %d Result", new Object[]{Integer.valueOf(dwSendIndex)}));
                byte[] ex = new byte[7];
                byte[] data = new byte[]{29, 40, 72, 6, 0, 48, 48, (byte)dwSendIndex, (byte)(dwSendIndex >> 8), (byte)(dwSendIndex >> 16), (byte)(dwSendIndex >> 24)};
                byte[] head = new byte[]{16, 4, 1, 16, 4, 1, 16, 4, 1, 16, 4, 1};
                byte[] cmd = new byte[head.length + data.length];
                byte offset = 0;
                System.arraycopy(head, 0, cmd, offset, head.length);
                int offset1 = offset + head.length;
                System.arraycopy(data, 0, cmd, offset1, data.length);
                int var10000 = offset1 + data.length;
                this.IO.SkipAvailable();
                if(this.IO.Write(cmd, 0, cmd.length) == cmd.length) {
                    long beginTime = System.currentTimeMillis();

                    label162:
                    while(true) {
                        do {
                            do {
                                while(true) {
                                    int nBytesReaded;
                                    do {
                                        if(!this.IO.IsOpened() || System.currentTimeMillis() - beginTime > (long)timeout) {
                                            break label162;
                                        }

                                        nBytesReaded = this.IO.Read(ex, 0, 1, timeout);
                                        if(nBytesReaded < 0) {
                                            break label162;
                                        }
                                    } while(nBytesReaded != 1);

                                    if(ex[0] == 55) {
                                        break;
                                    }

                                    if((ex[0] & 18) == 18) {
                                        Log.i("Pos", String.format("Printer RT Status: %02X ", new Object[]{Byte.valueOf(ex[0])}));
                                        if((ex[0] & 8) != 0) {
                                            break label162;
                                        }
                                    }
                                }
                            } while(this.IO.Read(ex, 1, 1, timeout) != 1);
                        } while(ex[1] != 34 && ex[1] != 51);

                        if(this.IO.Read(ex, 2, 5, timeout) == 5) {
                            int dwRecvIndex = ex[2] & 255 | (ex[3] & 255) << 8 | (ex[4] & 255) << 16 | (ex[5] & 255) << 24;
                            if(dwSendIndex == dwRecvIndex) {
                                if(ex[1] == 34) {
                                    result = true;
                                }

                                Log.i("Pos", String.format("Ticket Result: %02X %02X %02X %02X %02X %02X %02X", new Object[]{Byte.valueOf(ex[0]), Byte.valueOf(ex[1]), Byte.valueOf(ex[2]), Byte.valueOf(ex[3]), Byte.valueOf(ex[4]), Byte.valueOf(ex[5]), Byte.valueOf(ex[6])}));
                                break;
                            }
                        }
                    }
                }

                Log.i("Pos", String.format("Ticket %d %s", new Object[]{Integer.valueOf(dwSendIndex), result?"Succeed":"Failed"}));
            } catch (Exception var16) {
                Log.i("Pos", var16.toString());
            } finally {
                this.IO.Unlock();
            }

            return result;
        }
    }

    private byte[] byteArraysToBytes(byte[][] data) {
        int length = 0;

        for(int send = 0; send < data.length; ++send) {
            length += data[send].length;
        }

        byte[] var7 = new byte[length];
        int k = 0;

        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data[i].length; ++j) {
                var7[k++] = data[i][j];
            }
        }

        return var7;
    }
}
