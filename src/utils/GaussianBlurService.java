package utils;

public final class GaussianBlurService {

    private final int length;
    private double[] kernelReal;
    private double[] kernelImag;

    private double[] cosTable;
    private double[] sinTable;
    private int[] bitRevTable;


    public GaussianBlurService(double[] krnReal) {
        length = krnReal.length;
        int levels = 31 - Integer.numberOfLeadingZeros(length);
        if (1 << levels != length)
            throw new IllegalArgumentException("Length is not a power of 2");

        cosTable = new double[length / 2];
        sinTable = new double[length / 2];
        for (int i = 0; i < cosTable.length; i++) {
            cosTable[i] = Math.cos(2 * Math.PI * i / length);
            sinTable[i] = Math.sin(2 * Math.PI * i / length);
        }

        bitRevTable = new int[length];
        for (int i = 0; i < length; i++)
            bitRevTable[i] = Integer.reverse(i) >>> (32 - levels);

        kernelReal = krnReal.clone();
        kernelImag = new double[length];
        transform(kernelReal, kernelImag);
    }

    public void convolve(double[] real, double[] imag) {
        if (real.length != length || imag.length != length)
            throw new IllegalArgumentException();
        transform(real, imag);
        for (int i = 0; i < length; i++) {
            double temp = real[i] * kernelReal[i] - imag[i] * kernelImag[i];
            imag[i]     = imag[i] * kernelReal[i] + real[i] * kernelImag[i];
            real[i]     = temp;
        }
        transform(imag, real);
    }


    private void transform(double[] real, double[] imag) {
        for (int i = 0; i < real.length; i++) {
            int j = bitRevTable[i];
            if (j > i) {
                double tpre = real[i];
                double tpim = imag[i];
                real[i] = real[j];
                imag[i] = imag[j];
                real[j] = tpre;
                imag[j] = tpim;
            }
        }

        for (int size = 2; size <= length; size *= 2) {
            int halfsize = size / 2;
            int tablestep = length / size;
            for (int i = 0; i < length; i += size) {
                for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
                    double tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
                    double tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
                    real[j + halfsize] = real[j] - tpre;
                    imag[j + halfsize] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }
            if (size == length)
                break;
        }
    }

}