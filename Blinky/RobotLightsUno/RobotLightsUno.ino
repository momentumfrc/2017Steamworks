#include <Wire.h>
#include <Adafruit_NeoPixel.h>
//#include <Serial.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

/*
 * I2C controlled NeoPixel driver
 * 
 * Receives bytestream over I2C and compiles RGB values for NeoPixel strip.
 * 
 * I2C address: 16
 * I2C register: 0x01
 * 
 * Reserved values:
 * - register address: 0x01
 * - start of frame: 0xfd
 * - end of frame: 0xfe
 * 
 * All other values can be used for RGB display.
 * 
 * Display protocol:
 * - Send one byte at a time from master using single-byte write to I2C device 16, register 0x01
 * - Begin frame by sending value 0xfd
 * - Send all RGB values in sequence: R, G, B, R, G, B, etc (using single-byte write as above)
 *   - Note: RGB values must be sanitized of reserved values before sending:
 *     - 0x01 should be pushed to 0x00
 *     - 0xfd and 0xfe should be pushed to 0xff.
 * - End frame and trigger display by sending value 0xfe
 * 
 * NeoPixels will hold the last value they received.  There is no required refresh rate.
 * 
 */

#define PIN 1

const uint32_t dimRED = 0x200000;
const uint32_t dimGREEN = 0x002000;
const uint32_t dimBLUE = 0x000020;
const uint32_t dimWHITE = 0x202020;

// Parameter 1 = number of pixels in strip
// Parameter 2 = Arduino pin number (most are valid)
// Parameter 3 = pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    Pixels are wired for RGBW bitstream (NeoPixel RGBW products)

// Max pixels on Trinket is 94 due to RAM limits
Adafruit_NeoPixel strip = Adafruit_NeoPixel(120, PIN, NEO_GRB + NEO_KHZ800);

// IMPORTANT: To reduce NeoPixel burnout risk, add 1000 uF capacitor across
// pixel power leads, add 300 - 500 Ohm resistor on first pixel's data input
// and minimize distance between Arduino and first pixel.  Avoid connecting
// on a live circuit...if you must, connect GND first.

int ix = 0;
uint32_t pixel = 0;

void setup() {
  strip.begin();
  for (int i = 0; i < strip.numPixels();)
  {
    strip.setPixelColor(i++, dimRED);
    strip.setPixelColor(i++, dimGREEN);
    strip.setPixelColor(i++, dimBLUE);
    strip.setPixelColor(i++, dimWHITE);
  }
  strip.show();       // Initialize all pixels to RGBW test pattern
  Wire.begin(16);     // join i2c bus with address #16
  Wire.onReceive(onReceive);
}

void loop() {
  delay(100);
}

void onReceive(int howMany) {
  while (Wire.available())
  {
    uint32_t b = Wire.read();
    if (b == 0x01)
    {
      // eat address byte
    }
    else if (b == 0xfd)
    {
      ix = 0;
    }
    else if (b == 0xfe)
    {
      strip.show();
    }
    else
    {
      if (ix < strip.numPixels()*3)
      {
        //pixel is RGB in big-endian order
        pixel |= b << (2 - ix%3)*8;
        ++ix;
        if (ix%3 == 0)
        {
          strip.setPixelColor(ix/3 - 1, pixel);
          pixel = 0;
        }
      }
    }
  }
}

