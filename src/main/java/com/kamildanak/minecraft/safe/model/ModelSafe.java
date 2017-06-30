package com.kamildanak.minecraft.safe.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelSafe  extends ModelBase{
    public ModelRenderer chestLid;
    public ModelRenderer chestBelow;
    public ModelRenderer chestKnob;

    public ModelSafe(){
    	this.chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
        this.chestLid.addBox(0.0F, -3.0F, -13.0F, 12, 3, 12, 0.0F);
        this.chestLid.rotationPointX = 1.0F;
        this.chestLid.rotationPointY = 3.0F;
        this.chestLid.rotationPointZ = 15.0F;
        this.chestKnob = (new ModelRenderer(this, 44, 0)).setTextureSize(64, 64);
        this.chestKnob.addBox(4.0F, -5.0F, -12.0F, 5, 3, 5, -1.0F);
        this.chestKnob.rotationPointX = 1.0F;
        this.chestKnob.rotationPointY = 3.0F;
        this.chestKnob.rotationPointZ = 15.0F;
        this.chestBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
        this.chestBelow.addBox(-1.0F, -5.0F, 0.0F, 14, 14, 14, 0.0F);
        this.chestBelow.rotationPointX = 1.0F;
        this.chestBelow.rotationPointY = 6.0F;
        this.chestBelow.rotationPointZ = 1.0F;
    }

    /**
     * This method renders out all parts of the chest model.
     */
    public void renderAll()
    {
        this.chestKnob.rotateAngleX = this.chestLid.rotateAngleX;
        
        this.chestLid.render(0.0625F);
        this.chestKnob.render(0.0625F);
        this.chestBelow.render(0.0625F);
    }
}
