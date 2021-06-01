import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class treequery {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		 // check for correct number of arguments
        if (args.length != constants.TREEQUERY_ARG_COUNT) {
            System.out.println("Error: Incorrect number of arguments were input");
            return;
        }
        
        //get the arguments passed from the command line
        String text = args[0];
        int pageSize = Integer.parseInt(args[constants.TREEQUERY_ARG_COUNT - 1]);
        
        //Initialize fields that are used
        String datafile = "index." + pageSize;
        long startTime = 0;
        long finishTime = 0;
        int numBytesInOneRecord = constants.INDEX_RECORD_SIZE;
        int numBytesInSdtnameField = constants.STD_NAME_SIZE;
        int numBytesIntField = Integer.BYTES;
        int numRecordsPerPage = pageSize/numBytesInOneRecord;
        byte[] page = new byte[pageSize];
        FileInputStream inStream = null;

        try {
            inStream = new FileInputStream(datafile);
            int numBytesRead = 0;
            startTime = System.nanoTime();
            // Create byte arrays for each field
            byte[] sdtnameBytes = new byte[numBytesInSdtnameField];
            byte[] pageNum = new byte[constants.HEAP_PAGE_NUM];
            byte[] position = new byte[constants.HEAP_PAGE_POSITION];

            // until the end of the binary file is reached
            while ((numBytesRead = inStream.read(page)) != -1) {
            	
                // Process each record in page
                for (int i = 0; i < numRecordsPerPage; i++) {

                    // Copy record's SdtName (field is located at multiples of the total record byte length)
                    System.arraycopy(page, (i*numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);

                    // Check if field is empty; if so,stop iterating
                    if (sdtnameBytes[0] == 0) {
                        // can stop checking records
                        break;
                    }

                    // Check for match to "text"
                    String sdtNameString = new String(sdtnameBytes);
                    
                    // if match is found, copy bytes of other fields and print out the record
                    if (sdtNameString.matches(text)) {
                    	
                    	System.out.println("pass here");
                    	
                    	//Display records if matches
						System.arraycopy(page, ((i*numBytesInOneRecord) + constants.PAGE_NUM_OFFSET), pageNum, 0, numBytesIntField);
						System.arraycopy(page, ((i*numBytesInOneRecord) + constants.PAGE_POSITION_OFFSET), position, 0, numBytesIntField);

                        // Get a string representation of the record for printing to stdout
                        String record = sdtNameString.trim() + "," + ByteBuffer.wrap(pageNum).getInt() 
                        		+ "," + ByteBuffer.wrap(position).getInt();
                        System.out.println(record);
                    }
                }
            }

            finishTime = System.nanoTime();
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found " + e.getMessage());
        }
        catch (IOException e) {
            System.err.println("IO Exception " + e.getMessage());
        }
        finally {

            if (inStream != null) {
                inStream.close();
            }
        }

        long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;
        System.out.println("Time taken: " + timeInMilliseconds + " ms");
    }

}
