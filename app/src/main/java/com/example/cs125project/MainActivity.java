package com.example.cs125project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    /**
     * If the user requests to take an image with their camera
     */
    static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * instance variable containing the image the user took a picture of.
     */
    private Bitmap userImage;

    /**
     * Width of the bitmap.
     */
    private int width;

    /**
     * Height of the bitmap.
     */
    private int height;

    /**
     * How much to scale the images by.
     */
    final private int SCALE = 100;

    /**
     * Array containing the integer values of the pictures in the drawable folder
     */
    private int[] pictureArray = {R.drawable.picture1, R.drawable.picture2, R.drawable.picture3, R.drawable.picture4,
            R.drawable.picture5, R.drawable.picture6, R.drawable.picture7, R.drawable.picture8,
            R.drawable.picture9, R.drawable.picture10, R.drawable.picture11, R.drawable.picture12,
            R.drawable.picture13, R.drawable.picture14, R.drawable.picture15, R.drawable.picture16,
            R.drawable.picture17, R.drawable.picture18};

    /**
     * Dictionary of the pictures with the scaled bitmaps used to create the mosaic.
     */
    private Map<Integer, Bitmap> picBitMap = new HashMap<>();


    /**
     * Dictionary of the pictures with the pixel array of the scaled bitmaps.
     */
    private Map<Integer, int[]> picPixels = new HashMap<>();

    private Map<Integer, int[]> avgColor = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button to take a picture. When clicked it opens the camera application on the phone.
        Button takePic = findViewById(R.id.takePicture);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        Button randomChallen = findViewById(R.id.randomChallen);
        randomChallen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomPic();
            }
        });

        //Button to convert the picture the user took into Challens.
        final Button convert = findViewById(R.id.convertChallen);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convert.setVisibility(View.GONE);
                challenMosaicCreator();
            }
        });

    }

    /**
     * Makes an intent and starts an activity to take a picture.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * This function runs when the user is done taking a picture. The image is converted into a bitmap.
     * @param requestCode The code to request
     * @param resultCode The resulting code
     * @param data The intent received from the picture
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Get the picture as a bitmap and store it in the instance variable.
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            System.out.print("Is mutable: ");
            System.out.println(imageBitmap.isMutable());
            userImage = imageBitmap;
            height = userImage.getHeight();
            width = userImage.getWidth();

            //Set the ImageView to the user's picture.
            ImageView challen = findViewById(R.id.challen);
            challen.setImageBitmap(imageBitmap);
            challen.setVisibility(View.VISIBLE);



            //Change the text on the take picture button.
            Button takePic = findViewById(R.id.takePicture);
            takePic.setText("Retake Picture");

            //Make the convert button visible.
            Button convert = findViewById(R.id.convertChallen);
            convert.setVisibility(View.VISIBLE);

            /**EditText challenText = findViewById(R.id.challenText);
            challenText.setText("SOOO MANY CHALLENS!");
            challenText.setVisibility(View.VISIBLE); */

        }
    }

    private void challenMosaicCreator() {
        /**
        BitmapDrawable pic1 = (BitmapDrawable) getDrawable(R.drawable.picture1);
        Bitmap bitPic1 = pic1.getBitmap();
        ImageView challen = findViewById(R.id.challen);
        challen.setImageBitmap(bitPic1);
         */

        //Clear the exisiting dictionary.
        picBitMap.clear();
        picPixels.clear();

        //Parse through the pictureArray and get a scaled down bitmap for use in the mosaic.
        for (int i: pictureArray) {
            Bitmap bitPic = ((BitmapDrawable) getDrawable(i)).getBitmap();
            //int scaledHeight = (height / SCALE);
            //int scaledWidth = (width / SCALE);
            //bitPic = bitPic.createScaledBitmap(bitPic, scaledWidth, scaledHeight, true);
            //bitPic = bitPic.createScaledBitmap(bitPic, 1, 1, true);
            Bitmap mutableBitPic = bitPic.createBitmap(bitPic.getWidth(), bitPic.getHeight(),
                    bitPic.getConfig());
            picBitMap.put(i, mutableBitPic);
        }

        //Parse through the recently made bitmap dictionary and get a pixel array of the scaled down images.
        //Add the pixel array to a new dictionary.
        for (Map.Entry<Integer, Bitmap> entry: picBitMap.entrySet()) {
            int[] pixelArray = new int[entry.getValue().getWidth()*entry.getValue().getHeight()];
            for (int y = 0; y < entry.getValue().getHeight(); y++) {
                for (int x = 0; x < entry.getValue().getWidth(); x++) {
                    pixelArray[x] = entry.getValue().getPixel(x, y);
                }
            }
            picPixels.put(entry.getKey(), pixelArray);
        }

        for (Map.Entry<Integer, int[]> entry: picPixels.entrySet()) {
            int sumPixel = 0;
            for (int i : entry.getValue()) {
                sumPixel += i;
            }
            int[] averageColor = {sumPixel / entry.getValue().length};
            avgColor.put(entry.getKey(), averageColor);
        }


        //Create the mosaic
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                userImage.setPixel(x, y, compare(userImage.getPixel(x, y)));
                //userImage.setPixels(picPixels.get(R.drawable.picture1), 0, 4, x, y, 4, 4);
            }
        }

        //userImage = picBitMap.get(R.drawable.picture1).copy(userImage.getConfig(), true);

        /**
        System.out.println(picPixels.get(R.drawable.picture1));
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                userImage.setPixel(x, y, userImage.getPixel(0,0));
            }
        } */

        ImageView challen = findViewById(R.id.challen);
        challen.setImageBitmap(userImage);
    }

    private void randomPic() {
        //Make a random integer from 0 to 17
        Random random = new Random();
        int randomInt = random.nextInt(18);

        //Turn the imageView into of the Challen pictures.
        Drawable challenDrawable = getDrawable(pictureArray[randomInt]);
        ImageView challen = findViewById(R.id.challen);
        challen.setImageDrawable(challenDrawable);
        challen.setVisibility(View.VISIBLE);

        //Set the instance variable to the random Challen pic that was chosen.
        BitmapDrawable bitMapDrawPic = (BitmapDrawable) getDrawable(pictureArray[randomInt]);
        Bitmap bitMapPic = bitMapDrawPic.getBitmap();
        userImage = bitMapPic;
        height = userImage.getHeight();
        width = userImage.getWidth();
        userImage = userImage.createBitmap(width, height, userImage.getConfig());


        /**
        //Make the picture the same size as the imageView
       bitMapPic = bitMapPic.createScaledBitmap(bitMapPic, challen.getWidth(), challen.getHeight(), , true);
        challen.setImageBitmap(bitMapPic);
        challen.setVisibility(View.VISIBLE);
         */

        //Make the convert button visible.
        Button convert = findViewById(R.id.convertChallen);
        convert.setVisibility(View.VISIBLE);

        //Change the text on the take picture button if it says "Retake Picture"
        Button takePic = findViewById(R.id.takePicture);
        if (takePic.getText().equals("Retake Picture")) {
            takePic.setText("Take Picture");
        }

    }

    /**
     * Compares a pixel in the image and returns a challen with the closest color.
     * @param pixel The pixel in the image
     * @return challen
     */
    private int compare(int pixel) {
        int minimum = 100000;
        int returnPixel = 0;

        for (Map.Entry<Integer, int[]> entry : avgColor.entrySet()) {
             if (minimum > Math.abs(pixel - entry.getValue()[0])) {
                 minimum = Math.abs(pixel - entry.getValue()[0]);
                 returnPixel = entry.getValue()[0];
             }
        }
        return returnPixel;
    }


}
