package com.example.asus.bakingFrag.Adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/*
 * Java Object that represents bakingFrag recipes. In contains also setter and getter method for this object.
 */
public class Recipes implements Parcelable {

    public static final Parcelable.Creator<Recipes> CREATOR = new Parcelable.Creator<Recipes>() {
        @Override
        public Recipes createFromParcel(Parcel source) {
            return new Recipes(source);
        }

        @Override
        public Recipes[] newArray(int size) {
            return new Recipes[size];
        }
    };
    @SerializedName("mName")
    String mName;
    @SerializedName("mImage")
    String mImage;
    @SerializedName("mServings")
    int mServings;
    @SerializedName("mIngredients")
    List<Ingredients> mIngredients;
    @SerializedName("mSteps")
    List<Steps> mSteps;

    public Recipes(String name, String image, int servings) {
        this.mName = name;
        this.mImage = image;
        this.mServings = servings;
    }

    protected Recipes(Parcel in) {
        this.mName = in.readString();
        this.mImage = in.readString();
        this.mServings = in.readInt();
        this.mIngredients = new ArrayList<Ingredients>();
        in.readList(this.mIngredients, Ingredients.class.getClassLoader());
        this.mSteps = new ArrayList<Steps>();
        in.readList(this.mSteps, Steps.class.getClassLoader());
    }

    public String getImage() {
        return mImage;
    }

    public String getName() {
        return mName;
    }

    public int getServings() {
        return mServings;
    }

    public void setIngredients(List<Ingredients> ingredientList) {
        mIngredients = ingredientList;
    }
    public List<Ingredients> getIngredients() {
       return mIngredients ;
    }

    public void setSteps(List<Steps> stepsList) {
        mSteps = stepsList;
    }
    public List<Steps> getSteps() {
        return mSteps;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mImage);
        dest.writeInt(this.mServings);
        dest.writeList(this.mIngredients);
        dest.writeList(this.mSteps);
    }
  /*
   * Static inner class for Recipe Ingredients
   */
    public static class Ingredients implements Parcelable {
        public static final Creator<Ingredients> CREATOR = new Creator<Ingredients>() {
            @Override
            public Ingredients createFromParcel(Parcel source) {
                return new Ingredients(source);
            }

            @Override
            public Ingredients[] newArray(int size) {
                return new Ingredients[size];
            }
        };
        @SerializedName("mQuantity")
        int mQuantity;
        @SerializedName("mMeasure")
        String mMeasure;
        @SerializedName("mIngredient")
        String mIngredient;

        public Ingredients(int quantity, String measure, String ingredient) {
            mQuantity = quantity;
            mMeasure = measure;
            mIngredient = ingredient;
        }

        protected Ingredients(Parcel in) {
            this.mQuantity = in.readInt();
            this.mMeasure = in.readString();
            this.mIngredient = in.readString();
        }

        public int getQuantity() {
            return mQuantity;
        }

        public String getMeasure() {
            return mMeasure;
        }

        public String getIngredient() {
            return mIngredient;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mQuantity);
            dest.writeString(this.mMeasure);
            dest.writeString(this.mIngredient);
        }
    }
    /*
     * Static inner class for Recipe Steps
     */
    public static class Steps implements Parcelable {
        @SerializedName("mShortDescription")
        String mShortDescription;
        @SerializedName("mDescription")
        String mDescription;
        @SerializedName("mVideoURL")
        String mVideoURL;
        @SerializedName("mThumbnailURL")
        String mThumbnailURL;

        public Steps(String shortdescription, String description, String videoURL, String thumbnailURL) {
            mShortDescription = shortdescription;
            mDescription = description;
            mVideoURL = videoURL;
            mThumbnailURL = thumbnailURL;
        }

        public String getShortDescription() {
            return mShortDescription;
        }

        public String getDescription() {
            return mDescription;
        }

        public String getVideoURL() {
            return mVideoURL;
        }

        public String getThumbnailURL() {
            return mThumbnailURL;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mShortDescription);
            dest.writeString(this.mDescription);
            dest.writeString(this.mVideoURL);
            dest.writeString(this.mThumbnailURL);
        }

        protected Steps(Parcel in) {
            this.mShortDescription = in.readString();
            this.mDescription = in.readString();
            this.mVideoURL = in.readString();
            this.mThumbnailURL = in.readString();
        }

        public static final Creator<Steps> CREATOR = new Creator<Steps>() {
            @Override
            public Steps createFromParcel(Parcel source) {
                return new Steps(source);
            }

            @Override
            public Steps[] newArray(int size) {
                return new Steps[size];
            }
        };
    }
}