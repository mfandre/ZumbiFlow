package Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class Instance {
	private Point data[] = null;

	public Instance(String file) {
		readData(file);
	}

    public Instance(weka.core.Instances wekaData){
        this.data = new Point[wekaData.numInstances()];
        int numCoords = wekaData.numAttributes();

        for(int i = 0; i < wekaData.numInstances(); i++){
            this.data[i] = new Point(numCoords);
            for(int j = 0; j < numCoords; j++) {
                this.data[i].getCoord()[j] = wekaData.instance(i).value(j);
            }
        }
    }

	public Point[] getData() {
		return data;
	}

	public void setData(Point[] data) {
		this.data = data;
	}

	public void readData(String file) {
		BufferedReader br = null;
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(new File(file)));
			int col = lnr.readLine().split(",").length;
			lnr.skip(Long.MAX_VALUE);
			int row = lnr.getLineNumber();
			this.data = new Point[row];
			for(int i=0;i<row;i++)
				this.data[i] = new Point(col);
			lnr.close();

			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));
			int i = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				for (int j = 0; j < col; j++) {
					String fields[] = sCurrentLine.split(",");
					this.data[i].getCoord()[j] = Float.parseFloat(fields[j]);
				}
				i++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
