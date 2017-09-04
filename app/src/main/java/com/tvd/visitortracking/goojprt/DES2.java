package com.tvd.visitortracking.goojprt;

public class DES2 {
    static final byte[] PC1_Table = new byte[]{57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};
    static final byte[] PC2_Table = new byte[]{14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};
    static final byte[] Shift_Table = new byte[]{1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    static final byte[] IP_Table = new byte[]{58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};
    static final byte[] E_Table = new byte[]{32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};
    static final byte[][][] S_Box = new byte[][][]{{{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7}, {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8}, {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0}, {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}}, {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10}, {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5}, {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15}, {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}}, {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8}, {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1}, {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7}, {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}}, {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15}, {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9}, {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4}, {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}}, {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9}, {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6}, {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14}, {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}}, {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11}, {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8}, {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6}, {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}}, {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1}, {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6}, {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2}, {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}}, {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7}, {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2}, {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8}, {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}}};
    static final byte[] P_Table = new byte[]{16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25};
    static final byte[] IPR_Table = new byte[]{40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
    byte[][] szSubKeys = new byte[16][48];
    byte[] szCiphertextRaw = new byte[64];
    byte[] szPlaintextRaw = new byte[64];
    byte[] szCiphertextInBytes = new byte[8];
    byte[] szPlaintextInBytes = new byte[8];
    byte[] szCiphertextInBinary = new byte[65];
    byte[] szCiphertextInHex = new byte[17];
    byte[] szPlaintext = new byte[9];

    DES2() {
    }

    public void yxyDES2_InitializeKey(byte[] srcBytes) {
        byte[] sz_64key = new byte[64];
        byte[] sz_56key = new byte[56];
        boolean k = false;
        this.yxyDES2_Bytes2Bits(srcBytes, sz_64key, 64);

        for(int var5 = 0; var5 < 56; ++var5) {
            sz_56key[var5] = sz_64key[PC1_Table[var5] - 1];
        }

        this.yxyDES2_CreateSubKey(sz_56key);
    }

    void yxyDES2_EncryptData(byte[] _srcBytes) {
        byte[] szSrcBits = new byte[64];
        byte[] sz_IP = new byte[64];
        byte[] sz_Li = new byte[32];
        byte[] sz_Ri = new byte[32];
        byte[] sz_Final64 = new byte[64];
        boolean i = false;
        boolean j = false;
        this.yxyDES2_Bytes2Bits(_srcBytes, szSrcBits, 64);
        this.yxyDES2_InitialPermuteData(szSrcBits, sz_IP);
        this.memcpy(sz_Li, 0, sz_IP, 0, 32);
        this.memcpy(sz_Ri, 0, sz_IP, 32, 32);

        for(int var9 = 0; var9 < 16; ++var9) {
            this.yxyDES2_FunctionF(sz_Li, sz_Ri, var9);
        }

        this.memcpy(sz_Final64, 0, sz_Ri, 0, 32);
        this.memcpy(sz_Final64, 32, sz_Li, 0, 32);

        for(int var10 = 0; var10 < 64; ++var10) {
            this.szCiphertextRaw[var10] = sz_Final64[IPR_Table[var10] - 1];
        }

        this.yxyDES2_Bits2Bytes(this.szCiphertextInBytes, this.szCiphertextRaw, 64);
    }

    void yxyDES2_DecryptData(byte[] _srcBytes) {
        byte[] szSrcBits = new byte[64];
        byte[] sz_IP = new byte[64];
        byte[] sz_Li = new byte[32];
        byte[] sz_Ri = new byte[32];
        byte[] sz_Final64 = new byte[64];
        boolean i = false;
        boolean j = false;
        this.yxyDES2_Bytes2Bits(_srcBytes, szSrcBits, 64);
        this.yxyDES2_InitialPermuteData(szSrcBits, sz_IP);
        this.memcpy(sz_Ri, 0, sz_IP, 0, 32);
        this.memcpy(sz_Li, 0, sz_IP, 32, 32);

        for(int var9 = 0; var9 < 16; ++var9) {
            this.yxyDES2_FunctionF(sz_Ri, sz_Li, 15 - var9);
        }

        this.memcpy(sz_Final64, 0, sz_Li, 0, 32);
        this.memcpy(sz_Final64, 32, sz_Ri, 0, 32);

        for(int var10 = 0; var10 < 64; ++var10) {
            this.szPlaintextRaw[var10] = sz_Final64[IPR_Table[var10] - 1];
        }

        this.yxyDES2_Bits2Bytes(this.szPlaintextInBytes, this.szPlaintextRaw, 64);
    }

    byte[] getPlaintext() {
        return this.szPlaintextInBytes;
    }

    public int yxyDES2_EncryptAnyLength(byte[] _srcBytes, byte[] dst, int _bytesLength) {
        boolean iParts = false;
        boolean iResidue = false;
        boolean i = false;
        int rsLen = 0;
        int dstIdx = 0;
        byte[] szLast8Bits = new byte[8];
        if(_bytesLength == 8) {
            this.yxyDES2_EncryptData(_srcBytes);
            this.memcpy(dst, 0, this.szCiphertextInBytes, 0, 8);
            dst[8] = 0;
            rsLen = 8;
        } else if(_bytesLength < 8) {
            byte[] _temp8bytes = new byte[8];
            this.memcpy(_temp8bytes, 0, _srcBytes, 0, _bytesLength);
            this.yxyDES2_EncryptData(_temp8bytes);
            this.memcpy(dst, 0, this.szCiphertextInBytes, 0, 8);
            dst[8] = 0;
            rsLen = 8;
        } else if(_bytesLength > 8) {
            int var11 = _bytesLength >> 3;
            int var12 = _bytesLength % 8;

            for(int var13 = 0; var13 < var11; ++var13) {
                this.memcpy(szLast8Bits, 0, _srcBytes, var13 * 8, 8);
                this.yxyDES2_EncryptData(szLast8Bits);
                this.memcpy(dst, dstIdx, this.szCiphertextInBytes, 0, 8);
                dstIdx += 8;
                rsLen += 8;
            }

            if(var12 != 0) {
                this.memset(szLast8Bits, 0, 8);
                this.memcpy(szLast8Bits, 0, _srcBytes, var11 * 8, var12);
                this.yxyDES2_EncryptData(szLast8Bits);
                this.memcpy(dst, 0, this.szCiphertextInBytes, 0, 8);
                dst[8] = 0;
                rsLen += 8;
            }
        }

        return rsLen;
    }

    public int yxyDES2_DecryptAnyLength(byte[] _srcBytes, byte[] dst, int _bytesLength) {
        boolean iParts = false;
        boolean iResidue = false;
        boolean i = false;
        int rsLen = 0;
        int dstIdx = 0;
        byte[] szLast8Bits = new byte[8];
        byte[] _temp8bytes = new byte[8];
        if(_bytesLength == 8) {
            this.yxyDES2_DecryptData(_srcBytes);
            this.memcpy(dst, 0, this.szPlaintextInBytes, 0, 8);
            dst[8] = 0;
            rsLen = 8;
        } else if(_bytesLength < 8) {
            this.memcpy(_temp8bytes, 0, _srcBytes, 0, 8);
            this.yxyDES2_DecryptData(_temp8bytes);
            this.memcpy(dst, 0, this.szPlaintextInBytes, 0, _bytesLength);
            dst[_bytesLength] = 0;
            rsLen = 8;
        } else if(_bytesLength > 8) {
            int var11 = _bytesLength >> 3;
            int var12 = _bytesLength % 8;

            for(int var13 = 0; var13 < var11; ++var13) {
                this.memcpy(szLast8Bits, 0, _srcBytes, var13 << 3, 8);
                this.yxyDES2_DecryptData(szLast8Bits);
                this.memcpy(dst, dstIdx, this.szPlaintextInBytes, 0, 8);
                dstIdx += 8;
                rsLen += 8;
            }

            if(var12 != 0) {
                this.memset(szLast8Bits, 0, 8);
                this.memcpy(szLast8Bits, 0, _srcBytes, var11 << 3, 8);
                this.yxyDES2_DecryptData(szLast8Bits);
                this.memcpy(dst, 0, this.szPlaintextInBytes, 0, var12);
                rsLen += 8;
            }

            dst[8] = 0;
        }

        return rsLen;
    }

    void yxyDES2_Bytes2Bits(byte[] srcBytes, byte[] dstBits, int sizeBits) {
        boolean i = false;

        for(int var5 = 0; var5 < sizeBits; ++var5) {
            dstBits[var5] = (byte)(((srcBytes[var5 >> 3] & 255) << (var5 & 7) & 128) >> 7);
        }

    }

    void yxyDES2_Bits2Bytes(byte[] dstBytes, byte[] srcBits, int sizeBits) {
        boolean i = false;
        this.memset(dstBytes, 0, sizeBits >> 3);

        for(int var5 = 0; var5 < sizeBits; ++var5) {
            dstBytes[var5 >> 3] = (byte)(dstBytes[var5 >> 3] | (srcBits[var5] & 255) << 7 - (var5 & 7));
        }

    }

    void yxyDES2_Int2Bits(int _src, byte[] dstBits) {
        boolean i = false;

        for(int var4 = 0; var4 < 4; ++var4) {
            dstBits[var4] = (byte)((_src << var4 & 8) >> 3);
        }

    }

    void yxyDES2_Bits2Hex(byte[] dstHex, byte[] srcBits, int sizeBits) {
    }

    void yxyDES2_Hex2Bits(byte[] srcHex, byte[] dstBits, int sizeBits) {
    }

    byte[] yxyDES2_GetCiphertextInBinary() {
        return null;
    }

    byte[] yxyDES2_GetCiphertextInHex() {
        return null;
    }

    byte[] yxyDES2_GetCiphertextInBytes() {
        return null;
    }

    byte[] yxyDES2_GetPlaintext() {
        return null;
    }

    void yxyDES2_CreateSubKey(byte[] sz_56key) {
        byte[] szTmpL = new byte[28];
        byte[] szTmpR = new byte[28];
        byte[] szCi = new byte[28];
        byte[] szDi = new byte[28];
        byte[] szTmp56 = new byte[56];
        boolean i = false;
        boolean j = false;
        this.memcpy(szTmpL, 0, sz_56key, 0, 28);
        this.memcpy(szTmpR, 0, sz_56key, 28, 28);

        for(int var9 = 0; var9 < 16; ++var9) {
            this.memcpy(szCi, 0, szTmpL, Shift_Table[var9], 28 - Shift_Table[var9]);
            this.memcpy(szCi, 28 - Shift_Table[var9], szTmpL, 0, Shift_Table[var9]);
            this.memcpy(szDi, 0, szTmpR, Shift_Table[var9], 28 - Shift_Table[var9]);
            this.memcpy(szDi, 28 - Shift_Table[var9], szTmpR, 0, Shift_Table[var9]);
            this.memcpy(szTmp56, 0, szCi, 0, 28);
            this.memcpy(szTmp56, 28, szDi, 0, 28);

            for(int var10 = 0; var10 < 48; ++var10) {
                this.szSubKeys[var9][var10] = szTmp56[PC2_Table[var10] - 1];
            }

            this.memcpy(szTmpL, 0, szCi, 0, 28);
            this.memcpy(szTmpR, 0, szDi, 0, 28);
        }

    }

    void yxyDES2_FunctionF(byte[] sz_Li, byte[] sz_Ri, int iKey) {
        byte[] sz_48R = new byte[48];
        byte[] sz_xor48 = new byte[48];
        byte[] sz_P32 = new byte[32];
        byte[] sz_Rii = new byte[32];
        byte[] sz_Key = new byte[48];
        byte[] s_Compress32 = new byte[32];
        this.memcpy(sz_Key, 0, this.szSubKeys[iKey], 0, 48);
        this.yxyDES2_ExpansionR(sz_Ri, sz_48R);
        this.yxyDES2_XOR(sz_48R, sz_Key, 48, sz_xor48);
        this.yxyDES2_CompressFuncS(sz_xor48, s_Compress32);
        this.yxyDES2_PermutationP(s_Compress32, sz_P32);
        this.yxyDES2_XOR(sz_P32, sz_Li, 32, sz_Rii);
        this.memcpy(sz_Li, 0, sz_Ri, 0, 32);
        this.memcpy(sz_Ri, 0, sz_Rii, 0, 32);
    }

    void yxyDES2_InitialPermuteData(byte[] _src, byte[] _dst) {
        boolean i = false;

        for(int var4 = 0; var4 < 64; ++var4) {
            _dst[var4] = _src[IP_Table[var4] - 1];
        }

    }

    void yxyDES2_ExpansionR(byte[] _src, byte[] _dst) {
        boolean i = false;

        for(int var4 = 0; var4 < 48; ++var4) {
            _dst[var4] = _src[E_Table[var4] - 1];
        }

    }

    void yxyDES2_XOR(byte[] szParam1, byte[] szParam2, int uiParamLength, byte[] szReturnValueBuffer) {
        boolean i = false;

        for(int var6 = 0; var6 < uiParamLength; ++var6) {
            szReturnValueBuffer[var6] = (byte)(szParam1[var6] & 255 ^ szParam2[var6] & 255);
        }

    }

    void yxyDES2_CompressFuncS(byte[] _src48, byte[] _dst32) {
        byte[][] bTemp = new byte[8][6];
        byte[] dstBits = new byte[4];
        boolean i = false;
        boolean iX = false;
        boolean iY = false;
        boolean j = false;

        for(int var9 = 0; var9 < 8; ++var9) {
            this.memcpy(bTemp[var9], 0, _src48, var9 * 6, 6);
            int var10 = (bTemp[var9][0] & 255) * 2 + (bTemp[var9][5] & 255);
            int var11 = 0;

            for(int var12 = 1; var12 < 5; ++var12) {
                var11 += (bTemp[var9][var12] & 255) << 4 - var12;
            }

            this.yxyDES2_Int2Bits(S_Box[var9][var10][var11], dstBits);
            this.memcpy(_dst32, var9 * 4, dstBits, 0, 4);
        }

    }

    void yxyDES2_PermutationP(byte[] _src, byte[] _dst) {
        boolean i = false;

        for(int var4 = 0; var4 < 32; ++var4) {
            _dst[var4] = _src[P_Table[var4] - 1];
        }

    }

    public void memcpy(byte[] dest, int desOffset, byte[] src, int srcOffset, int nLength) {
        for(int i = 0; i < nLength; ++i) {
            dest[desOffset + i] = src[srcOffset + i];
        }

    }

    public void memset(byte[] s, int ch, int n) {
        for(int i = 0; i < n; ++i) {
            s[i] = (byte)ch;
        }

    }
}
