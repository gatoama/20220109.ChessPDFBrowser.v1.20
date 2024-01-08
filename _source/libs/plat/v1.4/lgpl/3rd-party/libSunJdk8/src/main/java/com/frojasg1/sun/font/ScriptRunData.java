package com.frojasg1.sun.font;

public final class ScriptRunData {
   private static final int CHAR_START = 0;
   private static final int CHAR_LIMIT = 1114112;
   private static int cache = 0;
   private static final int[] data = new int[]{0, 0, 65, 25, 91, 0, 97, 25, 123, 0, 170, 25, 171, 0, 181, 14, 182, 0, 186, 25, 187, 0, 192, 25, 215, 0, 216, 25, 247, 0, 248, 25, 545, 0, 546, 25, 564, 0, 592, 25, 686, 0, 688, 25, 697, 0, 736, 25, 741, 0, 768, 1, 848, 0, 864, 1, 880, 0, 890, 14, 891, 0, 902, 14, 903, 0, 904, 14, 907, 0, 908, 14, 909, 0, 910, 14, 930, 0, 931, 14, 975, 0, 976, 14, 1014, 0, 1024, 8, 1154, 0, 1155, 8, 1159, 0, 1160, 1, 1162, 8, 1231, 0, 1232, 8, 1270, 0, 1272, 8, 1274, 0, 1280, 8, 1296, 0, 1329, 3, 1367, 0, 1369, 3, 1370, 0, 1377, 3, 1416, 0, 1425, 1, 1442, 0, 1443, 1, 1466, 0, 1467, 1, 1470, 0, 1471, 1, 1472, 0, 1473, 1, 1475, 0, 1476, 1, 1477, 0, 1488, 19, 1515, 0, 1520, 19, 1523, 0, 1569, 2, 1595, 0, 1601, 2, 1611, 1, 1622, 0, 1646, 2, 1648, 1, 1649, 2, 1748, 0, 1749, 2, 1750, 1, 1765, 2, 1767, 1, 1769, 0, 1770, 1, 1774, 0, 1786, 2, 1789, 0, 1808, 34, 1837, 0, 1840, 34, 1867, 0, 1920, 37, 1970, 0, 2305, 10, 2308, 0, 2309, 10, 2362, 0, 2364, 10, 2382, 0, 2384, 10, 2389, 0, 2392, 10, 2404, 0, 2406, 10, 2416, 0, 2433, 4, 2436, 0, 2437, 4, 2445, 0, 2447, 4, 2449, 0, 2451, 4, 2473, 0, 2474, 4, 2481, 0, 2482, 4, 2483, 0, 2486, 4, 2490, 0, 2492, 4, 2493, 0, 2494, 4, 2501, 0, 2503, 4, 2505, 0, 2507, 4, 2510, 0, 2519, 4, 2520, 0, 2524, 4, 2526, 0, 2527, 4, 2532, 0, 2534, 4, 2546, 0, 2562, 16, 2563, 0, 2565, 16, 2571, 0, 2575, 16, 2577, 0, 2579, 16, 2601, 0, 2602, 16, 2609, 0, 2610, 16, 2612, 0, 2613, 16, 2615, 0, 2616, 16, 2618, 0, 2620, 16, 2621, 0, 2622, 16, 2627, 0, 2631, 16, 2633, 0, 2635, 16, 2638, 0, 2649, 16, 2653, 0, 2654, 16, 2655, 0, 2662, 16, 2677, 0, 2689, 15, 2692, 0, 2693, 15, 2700, 0, 2701, 15, 2702, 0, 2703, 15, 2706, 0, 2707, 15, 2729, 0, 2730, 15, 2737, 0, 2738, 15, 2740, 0, 2741, 15, 2746, 0, 2748, 15, 2758, 0, 2759, 15, 2762, 0, 2763, 15, 2766, 0, 2768, 15, 2769, 0, 2784, 15, 2785, 0, 2790, 15, 2800, 0, 2817, 31, 2820, 0, 2821, 31, 2829, 0, 2831, 31, 2833, 0, 2835, 31, 2857, 0, 2858, 31, 2865, 0, 2866, 31, 2868, 0, 2870, 31, 2874, 0, 2876, 31, 2884, 0, 2887, 31, 2889, 0, 2891, 31, 2894, 0, 2902, 31, 2904, 0, 2908, 31, 2910, 0, 2911, 31, 2914, 0, 2918, 31, 2928, 0, 2946, 35, 2948, 0, 2949, 35, 2955, 0, 2958, 35, 2961, 0, 2962, 35, 2966, 0, 2969, 35, 2971, 0, 2972, 35, 2973, 0, 2974, 35, 2976, 0, 2979, 35, 2981, 0, 2984, 35, 2987, 0, 2990, 35, 2998, 0, 2999, 35, 3002, 0, 3006, 35, 3011, 0, 3014, 35, 3017, 0, 3018, 35, 3022, 0, 3031, 35, 3032, 0, 3047, 35, 3059, 0, 3073, 36, 3076, 0, 3077, 36, 3085, 0, 3086, 36, 3089, 0, 3090, 36, 3113, 0, 3114, 36, 3124, 0, 3125, 36, 3130, 0, 3134, 36, 3141, 0, 3142, 36, 3145, 0, 3146, 36, 3150, 0, 3157, 36, 3159, 0, 3168, 36, 3170, 0, 3174, 36, 3184, 0, 3202, 21, 3204, 0, 3205, 21, 3213, 0, 3214, 21, 3217, 0, 3218, 21, 3241, 0, 3242, 21, 3252, 0, 3253, 21, 3258, 0, 3262, 21, 3269, 0, 3270, 21, 3273, 0, 3274, 21, 3278, 0, 3285, 21, 3287, 0, 3294, 21, 3295, 0, 3296, 21, 3298, 0, 3302, 21, 3312, 0, 3330, 26, 3332, 0, 3333, 26, 3341, 0, 3342, 26, 3345, 0, 3346, 26, 3369, 0, 3370, 26, 3386, 0, 3390, 26, 3396, 0, 3398, 26, 3401, 0, 3402, 26, 3406, 0, 3415, 26, 3416, 0, 3424, 26, 3426, 0, 3430, 26, 3440, 0, 3458, 33, 3460, 0, 3461, 33, 3479, 0, 3482, 33, 3506, 0, 3507, 33, 3516, 0, 3517, 33, 3518, 0, 3520, 33, 3527, 0, 3530, 33, 3531, 0, 3535, 33, 3541, 0, 3542, 33, 3543, 0, 3544, 33, 3552, 0, 3570, 33, 3572, 0, 3585, 38, 3643, 0, 3648, 38, 3663, 0, 3664, 38, 3674, 0, 3713, 24, 3715, 0, 3716, 24, 3717, 0, 3719, 24, 3721, 0, 3722, 24, 3723, 0, 3725, 24, 3726, 0, 3732, 24, 3736, 0, 3737, 24, 3744, 0, 3745, 24, 3748, 0, 3749, 24, 3750, 0, 3751, 24, 3752, 0, 3754, 24, 3756, 0, 3757, 24, 3770, 0, 3771, 24, 3774, 0, 3776, 24, 3781, 0, 3782, 24, 3783, 0, 3784, 24, 3790, 0, 3792, 24, 3802, 0, 3804, 24, 3806, 0, 3840, 39, 3841, 0, 3864, 39, 3866, 0, 3872, 39, 3892, 0, 3893, 39, 3894, 0, 3895, 39, 3896, 0, 3897, 39, 3898, 0, 3904, 39, 3912, 0, 3913, 39, 3947, 0, 3953, 39, 3973, 0, 3974, 39, 3980, 0, 3984, 39, 3992, 0, 3993, 39, 4029, 0, 4038, 39, 4039, 0, 4096, 28, 4130, 0, 4131, 28, 4136, 0, 4137, 28, 4139, 0, 4140, 28, 4147, 0, 4150, 28, 4154, 0, 4160, 28, 4170, 0, 4176, 28, 4186, 0, 4256, 12, 4294, 0, 4304, 12, 4345, 0, 4352, 18, 4442, 0, 4447, 18, 4515, 0, 4520, 18, 4602, 0, 4608, 11, 4615, 0, 4616, 11, 4679, 0, 4680, 11, 4681, 0, 4682, 11, 4686, 0, 4688, 11, 4695, 0, 4696, 11, 4697, 0, 4698, 11, 4702, 0, 4704, 11, 4743, 0, 4744, 11, 4745, 0, 4746, 11, 4750, 0, 4752, 11, 4783, 0, 4784, 11, 4785, 0, 4786, 11, 4790, 0, 4792, 11, 4799, 0, 4800, 11, 4801, 0, 4802, 11, 4806, 0, 4808, 11, 4815, 0, 4816, 11, 4823, 0, 4824, 11, 4847, 0, 4848, 11, 4879, 0, 4880, 11, 4881, 0, 4882, 11, 4886, 0, 4888, 11, 4895, 0, 4896, 11, 4935, 0, 4936, 11, 4955, 0, 4969, 11, 4989, 0, 5024, 6, 5109, 0, 5121, 40, 5741, 0, 5743, 40, 5751, 0, 5761, 29, 5787, 0, 5792, 32, 5867, 0, 5870, 32, 5873, 0, 5888, 42, 5901, 0, 5902, 42, 5909, 0, 5920, 43, 5941, 0, 5952, 44, 5972, 0, 5984, 45, 5997, 0, 5998, 45, 6001, 0, 6002, 45, 6004, 0, 6016, 23, 6100, 0, 6112, 23, 6122, 0, 6155, 1, 6158, 0, 6160, 27, 6170, 0, 6176, 27, 6264, 0, 6272, 27, 6314, 0, 7680, 25, 7836, 0, 7840, 25, 7930, 0, 7936, 14, 7958, 0, 7960, 14, 7966, 0, 7968, 14, 8006, 0, 8008, 14, 8014, 0, 8016, 14, 8024, 0, 8025, 14, 8026, 0, 8027, 14, 8028, 0, 8029, 14, 8030, 0, 8031, 14, 8062, 0, 8064, 14, 8117, 0, 8118, 14, 8125, 0, 8126, 14, 8127, 0, 8130, 14, 8133, 0, 8134, 14, 8141, 0, 8144, 14, 8148, 0, 8150, 14, 8156, 0, 8160, 14, 8173, 0, 8178, 14, 8181, 0, 8182, 14, 8189, 0, 8305, 25, 8306, 0, 8319, 25, 8320, 0, 8400, 1, 8427, 0, 8486, 14, 8487, 0, 8490, 25, 8492, 0, 11904, 17, 11930, 0, 11931, 17, 12020, 0, 12032, 17, 12246, 0, 12293, 17, 12294, 0, 12295, 17, 12296, 0, 12321, 17, 12330, 1, 12336, 0, 12344, 17, 12348, 0, 12353, 20, 12439, 0, 12441, 1, 12443, 0, 12445, 20, 12448, 0, 12449, 22, 12539, 0, 12541, 22, 12544, 0, 12549, 5, 12589, 0, 12593, 18, 12687, 0, 12704, 5, 12728, 0, 12784, 22, 12800, 0, 13312, 17, 19894, 0, 19968, 17, 40870, 0, 40960, 41, 42125, 0, 42128, 41, 42146, 0, 42148, 41, 42164, 0, 42165, 41, 42177, 0, 42178, 41, 42181, 0, 42182, 41, 42183, 0, 44032, 18, 55204, 0, 63744, 17, 64046, 0, 64048, 17, 64107, 0, 64256, 25, 64263, 0, 64275, 3, 64280, 0, 64285, 19, 64286, 1, 64287, 19, 64297, 0, 64298, 19, 64311, 0, 64312, 19, 64317, 0, 64318, 19, 64319, 0, 64320, 19, 64322, 0, 64323, 19, 64325, 0, 64326, 19, 64336, 2, 64434, 0, 64467, 2, 64830, 0, 64848, 2, 64912, 0, 64914, 2, 64968, 0, 65008, 2, 65020, 0, 65024, 1, 65040, 0, 65056, 1, 65060, 0, 65136, 2, 65141, 0, 65142, 2, 65277, 0, 65313, 25, 65339, 0, 65345, 25, 65371, 0, 65382, 22, 65392, 0, 65393, 22, 65438, 0, 65440, 18, 65471, 0, 65474, 18, 65480, 0, 65482, 18, 65488, 0, 65490, 18, 65496, 0, 65498, 18, 65501, 0, 66304, 30, 66335, 0, 66352, 13, 66379, 0, 66560, 9, 66598, 0, 66600, 9, 66638, 0, 119143, 1, 119146, 0, 119163, 1, 119171, 0, 119173, 1, 119180, 0, 119210, 1, 119214, 0, 131072, 17, 173783, 0, 194560, 17, 195102, 0, 1114112, -1};
   private static final int dataPower = 1024;
   private static final int dataExtra;

   private ScriptRunData() {
   }

   public static final int getScript(int var0) {
      if (var0 >= data[cache] && var0 < data[cache + 2]) {
         return data[cache + 1];
      } else if (var0 >= 0 && var0 < 1114112) {
         int var1 = 1024;
         int var2 = 0;
         if (var0 >= data[dataExtra]) {
            var2 = dataExtra;
         }

         while(var1 > 2) {
            var1 >>= 1;
            if (var0 >= data[var2 + var1]) {
               var2 += var1;
            }
         }

         cache = var2;
         return data[var2 + 1];
      } else {
         throw new IllegalArgumentException(Integer.toString(var0));
      }
   }

   static {
      dataExtra = data.length - 1024;
   }
}
