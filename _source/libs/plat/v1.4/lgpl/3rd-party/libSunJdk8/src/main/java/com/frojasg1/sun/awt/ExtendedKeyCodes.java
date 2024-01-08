package com.frojasg1.sun.awt;

import java.util.HashMap;
import java.util.HashSet;

public class ExtendedKeyCodes {
   private static final HashMap<Integer, Integer> regularKeyCodesMap = new HashMap(98, 1.0F);
   private static final HashSet<Integer> extendedKeyCodesSet = new HashSet(501, 1.0F);

   public ExtendedKeyCodes() {
   }

   public static final int getExtendedKeyCodeForChar(int var0) {
      int var1 = Character.toUpperCase(var0);
      int var2 = Character.toLowerCase(var0);
      if (regularKeyCodesMap.containsKey(var0)) {
         return regularKeyCodesMap.containsKey(var1) ? (Integer)regularKeyCodesMap.get(var1) : (Integer)regularKeyCodesMap.get(var0);
      } else {
         var1 += 16777216;
         var2 += 16777216;
         if (extendedKeyCodesSet.contains(var1)) {
            return var1;
         } else {
            return extendedKeyCodesSet.contains(var2) ? var2 : 0;
         }
      }
   }

   static {
      regularKeyCodesMap.put(8, 8);
      regularKeyCodesMap.put(9, 9);
      regularKeyCodesMap.put(10, 10);
      regularKeyCodesMap.put(27, 27);
      regularKeyCodesMap.put(8364, 516);
      regularKeyCodesMap.put(32, 32);
      regularKeyCodesMap.put(33, 517);
      regularKeyCodesMap.put(34, 152);
      regularKeyCodesMap.put(35, 520);
      regularKeyCodesMap.put(36, 515);
      regularKeyCodesMap.put(38, 150);
      regularKeyCodesMap.put(39, 222);
      regularKeyCodesMap.put(40, 519);
      regularKeyCodesMap.put(41, 522);
      regularKeyCodesMap.put(42, 151);
      regularKeyCodesMap.put(43, 521);
      regularKeyCodesMap.put(44, 44);
      regularKeyCodesMap.put(45, 45);
      regularKeyCodesMap.put(46, 46);
      regularKeyCodesMap.put(47, 47);
      regularKeyCodesMap.put(48, 48);
      regularKeyCodesMap.put(49, 49);
      regularKeyCodesMap.put(50, 50);
      regularKeyCodesMap.put(51, 51);
      regularKeyCodesMap.put(52, 52);
      regularKeyCodesMap.put(53, 53);
      regularKeyCodesMap.put(54, 54);
      regularKeyCodesMap.put(55, 55);
      regularKeyCodesMap.put(56, 56);
      regularKeyCodesMap.put(57, 57);
      regularKeyCodesMap.put(58, 513);
      regularKeyCodesMap.put(59, 59);
      regularKeyCodesMap.put(60, 153);
      regularKeyCodesMap.put(61, 61);
      regularKeyCodesMap.put(62, 160);
      regularKeyCodesMap.put(64, 512);
      regularKeyCodesMap.put(65, 65);
      regularKeyCodesMap.put(66, 66);
      regularKeyCodesMap.put(67, 67);
      regularKeyCodesMap.put(68, 68);
      regularKeyCodesMap.put(69, 69);
      regularKeyCodesMap.put(70, 70);
      regularKeyCodesMap.put(71, 71);
      regularKeyCodesMap.put(72, 72);
      regularKeyCodesMap.put(73, 73);
      regularKeyCodesMap.put(74, 74);
      regularKeyCodesMap.put(75, 75);
      regularKeyCodesMap.put(76, 76);
      regularKeyCodesMap.put(77, 77);
      regularKeyCodesMap.put(78, 78);
      regularKeyCodesMap.put(79, 79);
      regularKeyCodesMap.put(80, 80);
      regularKeyCodesMap.put(81, 81);
      regularKeyCodesMap.put(82, 82);
      regularKeyCodesMap.put(83, 83);
      regularKeyCodesMap.put(84, 84);
      regularKeyCodesMap.put(85, 85);
      regularKeyCodesMap.put(86, 86);
      regularKeyCodesMap.put(87, 87);
      regularKeyCodesMap.put(88, 88);
      regularKeyCodesMap.put(89, 89);
      regularKeyCodesMap.put(90, 90);
      regularKeyCodesMap.put(91, 91);
      regularKeyCodesMap.put(92, 92);
      regularKeyCodesMap.put(93, 93);
      regularKeyCodesMap.put(94, 514);
      regularKeyCodesMap.put(95, 523);
      regularKeyCodesMap.put(96, 192);
      regularKeyCodesMap.put(97, 65);
      regularKeyCodesMap.put(98, 66);
      regularKeyCodesMap.put(99, 67);
      regularKeyCodesMap.put(100, 68);
      regularKeyCodesMap.put(101, 69);
      regularKeyCodesMap.put(102, 70);
      regularKeyCodesMap.put(103, 71);
      regularKeyCodesMap.put(104, 72);
      regularKeyCodesMap.put(105, 73);
      regularKeyCodesMap.put(106, 74);
      regularKeyCodesMap.put(107, 75);
      regularKeyCodesMap.put(108, 76);
      regularKeyCodesMap.put(109, 77);
      regularKeyCodesMap.put(110, 78);
      regularKeyCodesMap.put(111, 79);
      regularKeyCodesMap.put(112, 80);
      regularKeyCodesMap.put(113, 81);
      regularKeyCodesMap.put(114, 82);
      regularKeyCodesMap.put(115, 83);
      regularKeyCodesMap.put(116, 84);
      regularKeyCodesMap.put(117, 85);
      regularKeyCodesMap.put(118, 86);
      regularKeyCodesMap.put(119, 87);
      regularKeyCodesMap.put(120, 88);
      regularKeyCodesMap.put(121, 89);
      regularKeyCodesMap.put(122, 90);
      regularKeyCodesMap.put(123, 161);
      regularKeyCodesMap.put(125, 162);
      regularKeyCodesMap.put(127, 127);
      regularKeyCodesMap.put(161, 518);
      extendedKeyCodesSet.add(16777312);
      extendedKeyCodesSet.add(16777340);
      extendedKeyCodesSet.add(16777342);
      extendedKeyCodesSet.add(16777378);
      extendedKeyCodesSet.add(16777379);
      extendedKeyCodesSet.add(16777381);
      extendedKeyCodesSet.add(16777383);
      extendedKeyCodesSet.add(16777384);
      extendedKeyCodesSet.add(16777387);
      extendedKeyCodesSet.add(16777392);
      extendedKeyCodesSet.add(16777393);
      extendedKeyCodesSet.add(16777394);
      extendedKeyCodesSet.add(16777395);
      extendedKeyCodesSet.add(16777396);
      extendedKeyCodesSet.add(16777397);
      extendedKeyCodesSet.add(16777398);
      extendedKeyCodesSet.add(16777399);
      extendedKeyCodesSet.add(16777401);
      extendedKeyCodesSet.add(16777402);
      extendedKeyCodesSet.add(16777403);
      extendedKeyCodesSet.add(16777404);
      extendedKeyCodesSet.add(16777405);
      extendedKeyCodesSet.add(16777406);
      extendedKeyCodesSet.add(16777407);
      extendedKeyCodesSet.add(16777412);
      extendedKeyCodesSet.add(16777413);
      extendedKeyCodesSet.add(16777414);
      extendedKeyCodesSet.add(16777415);
      extendedKeyCodesSet.add(16777425);
      extendedKeyCodesSet.add(16777430);
      extendedKeyCodesSet.add(16777431);
      extendedKeyCodesSet.add(16777432);
      extendedKeyCodesSet.add(16777439);
      extendedKeyCodesSet.add(16777440);
      extendedKeyCodesSet.add(16777441);
      extendedKeyCodesSet.add(16777442);
      extendedKeyCodesSet.add(16777444);
      extendedKeyCodesSet.add(16777445);
      extendedKeyCodesSet.add(16777446);
      extendedKeyCodesSet.add(16777447);
      extendedKeyCodesSet.add(16777448);
      extendedKeyCodesSet.add(16777449);
      extendedKeyCodesSet.add(16777450);
      extendedKeyCodesSet.add(16777451);
      extendedKeyCodesSet.add(16777452);
      extendedKeyCodesSet.add(16777453);
      extendedKeyCodesSet.add(16777454);
      extendedKeyCodesSet.add(16777456);
      extendedKeyCodesSet.add(16777457);
      extendedKeyCodesSet.add(16777458);
      extendedKeyCodesSet.add(16777459);
      extendedKeyCodesSet.add(16777460);
      extendedKeyCodesSet.add(16777461);
      extendedKeyCodesSet.add(16777462);
      extendedKeyCodesSet.add(16777463);
      extendedKeyCodesSet.add(16777464);
      extendedKeyCodesSet.add(16777465);
      extendedKeyCodesSet.add(16777466);
      extendedKeyCodesSet.add(16777467);
      extendedKeyCodesSet.add(16777468);
      extendedKeyCodesSet.add(16777469);
      extendedKeyCodesSet.add(16777470);
      extendedKeyCodesSet.add(16777477);
      extendedKeyCodesSet.add(16777947);
      extendedKeyCodesSet.add(16777538);
      extendedKeyCodesSet.add(16777534);
      extendedKeyCodesSet.add(16777563);
      extendedKeyCodesSet.add(16777569);
      extendedKeyCodesSet.add(16777567);
      extendedKeyCodesSet.add(16777573);
      extendedKeyCodesSet.add(16777598);
      extendedKeyCodesSet.add(16777596);
      extendedKeyCodesSet.add(16777475);
      extendedKeyCodesSet.add(16777479);
      extendedKeyCodesSet.add(16777485);
      extendedKeyCodesSet.add(16777497);
      extendedKeyCodesSet.add(16777499);
      extendedKeyCodesSet.add(16777489);
      extendedKeyCodesSet.add(16777544);
      extendedKeyCodesSet.add(16777553);
      extendedKeyCodesSet.add(16777585);
      extendedKeyCodesSet.add(16777561);
      extendedKeyCodesSet.add(16777583);
      extendedKeyCodesSet.add(16777571);
      extendedKeyCodesSet.add(16777945);
      extendedKeyCodesSet.add(16777520);
      extendedKeyCodesSet.add(16777511);
      extendedKeyCodesSet.add(16777509);
      extendedKeyCodesSet.add(16777521);
      extendedKeyCodesSet.add(16777503);
      extendedKeyCodesSet.add(16777525);
      extendedKeyCodesSet.add(16777483);
      extendedKeyCodesSet.add(16777481);
      extendedKeyCodesSet.add(16777505);
      extendedKeyCodesSet.add(16777501);
      extendedKeyCodesSet.add(16777581);
      extendedKeyCodesSet.add(16777565);
      extendedKeyCodesSet.add(16777528);
      extendedKeyCodesSet.add(16777559);
      extendedKeyCodesSet.add(16777532);
      extendedKeyCodesSet.add(16777491);
      extendedKeyCodesSet.add(16777507);
      extendedKeyCodesSet.add(16777575);
      extendedKeyCodesSet.add(16777547);
      extendedKeyCodesSet.add(16777473);
      extendedKeyCodesSet.add(16777519);
      extendedKeyCodesSet.add(16777495);
      extendedKeyCodesSet.add(16777515);
      extendedKeyCodesSet.add(16777542);
      extendedKeyCodesSet.add(16777549);
      extendedKeyCodesSet.add(16777527);
      extendedKeyCodesSet.add(16777587);
      extendedKeyCodesSet.add(16777579);
      extendedKeyCodesSet.add(16777555);
      extendedKeyCodesSet.add(16789756);
      extendedKeyCodesSet.add(16789666);
      extendedKeyCodesSet.add(16789668);
      extendedKeyCodesSet.add(16789670);
      extendedKeyCodesSet.add(16789672);
      extendedKeyCodesSet.add(16789674);
      extendedKeyCodesSet.add(16789675);
      extendedKeyCodesSet.add(16789677);
      extendedKeyCodesSet.add(16789679);
      extendedKeyCodesSet.add(16789681);
      extendedKeyCodesSet.add(16789683);
      extendedKeyCodesSet.add(16789685);
      extendedKeyCodesSet.add(16789687);
      extendedKeyCodesSet.add(16789689);
      extendedKeyCodesSet.add(16789691);
      extendedKeyCodesSet.add(16789693);
      extendedKeyCodesSet.add(16789695);
      extendedKeyCodesSet.add(16789697);
      extendedKeyCodesSet.add(16789700);
      extendedKeyCodesSet.add(16789702);
      extendedKeyCodesSet.add(16789704);
      extendedKeyCodesSet.add(16789706);
      extendedKeyCodesSet.add(16789707);
      extendedKeyCodesSet.add(16789708);
      extendedKeyCodesSet.add(16789709);
      extendedKeyCodesSet.add(16789710);
      extendedKeyCodesSet.add(16789711);
      extendedKeyCodesSet.add(16789714);
      extendedKeyCodesSet.add(16789717);
      extendedKeyCodesSet.add(16789720);
      extendedKeyCodesSet.add(16789723);
      extendedKeyCodesSet.add(16789726);
      extendedKeyCodesSet.add(16789727);
      extendedKeyCodesSet.add(16789728);
      extendedKeyCodesSet.add(16789729);
      extendedKeyCodesSet.add(16789730);
      extendedKeyCodesSet.add(16789732);
      extendedKeyCodesSet.add(16789734);
      extendedKeyCodesSet.add(16789736);
      extendedKeyCodesSet.add(16789737);
      extendedKeyCodesSet.add(16789738);
      extendedKeyCodesSet.add(16789739);
      extendedKeyCodesSet.add(16789740);
      extendedKeyCodesSet.add(16789741);
      extendedKeyCodesSet.add(16789743);
      extendedKeyCodesSet.add(16789747);
      extendedKeyCodesSet.add(16789659);
      extendedKeyCodesSet.add(16789660);
      extendedKeyCodesSet.add(16778992);
      extendedKeyCodesSet.add(16778993);
      extendedKeyCodesSet.add(16778994);
      extendedKeyCodesSet.add(16778995);
      extendedKeyCodesSet.add(16778996);
      extendedKeyCodesSet.add(16778997);
      extendedKeyCodesSet.add(16778998);
      extendedKeyCodesSet.add(16778999);
      extendedKeyCodesSet.add(16779000);
      extendedKeyCodesSet.add(16779001);
      extendedKeyCodesSet.add(16778864);
      extendedKeyCodesSet.add(16778878);
      extendedKeyCodesSet.add(16778886);
      extendedKeyCodesSet.add(16778764);
      extendedKeyCodesSet.add(16778964);
      extendedKeyCodesSet.add(16778848);
      extendedKeyCodesSet.add(16778849);
      extendedKeyCodesSet.add(16778850);
      extendedKeyCodesSet.add(16778851);
      extendedKeyCodesSet.add(16778852);
      extendedKeyCodesSet.add(16778853);
      extendedKeyCodesSet.add(16778854);
      extendedKeyCodesSet.add(16778855);
      extendedKeyCodesSet.add(16778856);
      extendedKeyCodesSet.add(16778857);
      extendedKeyCodesSet.add(16778779);
      extendedKeyCodesSet.add(16778785);
      extendedKeyCodesSet.add(16778788);
      extendedKeyCodesSet.add(16778790);
      extendedKeyCodesSet.add(16778791);
      extendedKeyCodesSet.add(16778792);
      extendedKeyCodesSet.add(16778793);
      extendedKeyCodesSet.add(16778794);
      extendedKeyCodesSet.add(16778795);
      extendedKeyCodesSet.add(16778796);
      extendedKeyCodesSet.add(16778797);
      extendedKeyCodesSet.add(16778798);
      extendedKeyCodesSet.add(16778799);
      extendedKeyCodesSet.add(16778800);
      extendedKeyCodesSet.add(16778801);
      extendedKeyCodesSet.add(16778802);
      extendedKeyCodesSet.add(16778803);
      extendedKeyCodesSet.add(16778804);
      extendedKeyCodesSet.add(16778805);
      extendedKeyCodesSet.add(16778806);
      extendedKeyCodesSet.add(16778807);
      extendedKeyCodesSet.add(16778808);
      extendedKeyCodesSet.add(16778809);
      extendedKeyCodesSet.add(16778810);
      extendedKeyCodesSet.add(16778817);
      extendedKeyCodesSet.add(16778818);
      extendedKeyCodesSet.add(16778819);
      extendedKeyCodesSet.add(16778820);
      extendedKeyCodesSet.add(16778821);
      extendedKeyCodesSet.add(16778822);
      extendedKeyCodesSet.add(16778823);
      extendedKeyCodesSet.add(16778824);
      extendedKeyCodesSet.add(16778825);
      extendedKeyCodesSet.add(16778826);
      extendedKeyCodesSet.add(16778830);
      extendedKeyCodesSet.add(16778831);
      extendedKeyCodesSet.add(16778832);
      extendedKeyCodesSet.add(16778834);
      extendedKeyCodesSet.add(16778904);
      extendedKeyCodesSet.add(16778916);
      extendedKeyCodesSet.add(16778921);
      extendedKeyCodesSet.add(16778927);
      extendedKeyCodesSet.add(16778942);
      extendedKeyCodesSet.add(16778956);
      extendedKeyCodesSet.add(16778956);
      extendedKeyCodesSet.add(16778962);
      extendedKeyCodesSet.add(16778387);
      extendedKeyCodesSet.add(16778391);
      extendedKeyCodesSet.add(16778395);
      extendedKeyCodesSet.add(16778397);
      extendedKeyCodesSet.add(16778403);
      extendedKeyCodesSet.add(16778415);
      extendedKeyCodesSet.add(16778417);
      extendedKeyCodesSet.add(16778419);
      extendedKeyCodesSet.add(16778425);
      extendedKeyCodesSet.add(16778427);
      extendedKeyCodesSet.add(16778457);
      extendedKeyCodesSet.add(16778473);
      extendedKeyCodesSet.add(16778322);
      extendedKeyCodesSet.add(16778323);
      extendedKeyCodesSet.add(16778321);
      extendedKeyCodesSet.add(16778324);
      extendedKeyCodesSet.add(16778325);
      extendedKeyCodesSet.add(16778326);
      extendedKeyCodesSet.add(16778327);
      extendedKeyCodesSet.add(16778328);
      extendedKeyCodesSet.add(16778329);
      extendedKeyCodesSet.add(16778330);
      extendedKeyCodesSet.add(16778331);
      extendedKeyCodesSet.add(16778332);
      extendedKeyCodesSet.add(16778385);
      extendedKeyCodesSet.add(16778334);
      extendedKeyCodesSet.add(16778335);
      extendedKeyCodesSet.add(16785686);
      extendedKeyCodesSet.add(16778318);
      extendedKeyCodesSet.add(16778288);
      extendedKeyCodesSet.add(16778289);
      extendedKeyCodesSet.add(16778310);
      extendedKeyCodesSet.add(16778292);
      extendedKeyCodesSet.add(16778293);
      extendedKeyCodesSet.add(16778308);
      extendedKeyCodesSet.add(16778291);
      extendedKeyCodesSet.add(16778309);
      extendedKeyCodesSet.add(16778296);
      extendedKeyCodesSet.add(16778297);
      extendedKeyCodesSet.add(16778298);
      extendedKeyCodesSet.add(16778299);
      extendedKeyCodesSet.add(16778300);
      extendedKeyCodesSet.add(16778301);
      extendedKeyCodesSet.add(16778302);
      extendedKeyCodesSet.add(16778303);
      extendedKeyCodesSet.add(16778319);
      extendedKeyCodesSet.add(16778304);
      extendedKeyCodesSet.add(16778305);
      extendedKeyCodesSet.add(16778306);
      extendedKeyCodesSet.add(16778307);
      extendedKeyCodesSet.add(16778294);
      extendedKeyCodesSet.add(16778290);
      extendedKeyCodesSet.add(16778316);
      extendedKeyCodesSet.add(16778315);
      extendedKeyCodesSet.add(16778295);
      extendedKeyCodesSet.add(16778312);
      extendedKeyCodesSet.add(16778317);
      extendedKeyCodesSet.add(16778313);
      extendedKeyCodesSet.add(16778311);
      extendedKeyCodesSet.add(16778314);
      extendedKeyCodesSet.add(16785429);
      extendedKeyCodesSet.add(16778161);
      extendedKeyCodesSet.add(16778162);
      extendedKeyCodesSet.add(16778163);
      extendedKeyCodesSet.add(16778164);
      extendedKeyCodesSet.add(16778165);
      extendedKeyCodesSet.add(16778166);
      extendedKeyCodesSet.add(16778167);
      extendedKeyCodesSet.add(16778168);
      extendedKeyCodesSet.add(16778169);
      extendedKeyCodesSet.add(16778170);
      extendedKeyCodesSet.add(16778171);
      extendedKeyCodesSet.add(16778172);
      extendedKeyCodesSet.add(16778173);
      extendedKeyCodesSet.add(16778174);
      extendedKeyCodesSet.add(16778175);
      extendedKeyCodesSet.add(16778176);
      extendedKeyCodesSet.add(16778177);
      extendedKeyCodesSet.add(16778179);
      extendedKeyCodesSet.add(16778178);
      extendedKeyCodesSet.add(16778180);
      extendedKeyCodesSet.add(16778181);
      extendedKeyCodesSet.add(16778182);
      extendedKeyCodesSet.add(16778183);
      extendedKeyCodesSet.add(16778184);
      extendedKeyCodesSet.add(16778185);
      extendedKeyCodesSet.add(16785808);
      extendedKeyCodesSet.add(16785810);
      extendedKeyCodesSet.add(16785811);
      extendedKeyCodesSet.add(16785427);
      extendedKeyCodesSet.add(16785436);
      extendedKeyCodesSet.add(16785437);
      extendedKeyCodesSet.add(16785438);
      extendedKeyCodesSet.add(16778704);
      extendedKeyCodesSet.add(16778705);
      extendedKeyCodesSet.add(16778706);
      extendedKeyCodesSet.add(16778707);
      extendedKeyCodesSet.add(16778708);
      extendedKeyCodesSet.add(16778709);
      extendedKeyCodesSet.add(16778710);
      extendedKeyCodesSet.add(16778711);
      extendedKeyCodesSet.add(16778712);
      extendedKeyCodesSet.add(16778713);
      extendedKeyCodesSet.add(16778714);
      extendedKeyCodesSet.add(16778715);
      extendedKeyCodesSet.add(16778716);
      extendedKeyCodesSet.add(16778717);
      extendedKeyCodesSet.add(16778718);
      extendedKeyCodesSet.add(16778719);
      extendedKeyCodesSet.add(16778720);
      extendedKeyCodesSet.add(16778721);
      extendedKeyCodesSet.add(16778722);
      extendedKeyCodesSet.add(16778723);
      extendedKeyCodesSet.add(16778724);
      extendedKeyCodesSet.add(16778725);
      extendedKeyCodesSet.add(16778726);
      extendedKeyCodesSet.add(16778727);
      extendedKeyCodesSet.add(16778728);
      extendedKeyCodesSet.add(16778729);
      extendedKeyCodesSet.add(16778730);
      extendedKeyCodesSet.add(16780801);
      extendedKeyCodesSet.add(16780802);
      extendedKeyCodesSet.add(16780803);
      extendedKeyCodesSet.add(16780804);
      extendedKeyCodesSet.add(16780805);
      extendedKeyCodesSet.add(16780807);
      extendedKeyCodesSet.add(16780808);
      extendedKeyCodesSet.add(16780810);
      extendedKeyCodesSet.add(16780812);
      extendedKeyCodesSet.add(16780820);
      extendedKeyCodesSet.add(16780821);
      extendedKeyCodesSet.add(16780822);
      extendedKeyCodesSet.add(16780823);
      extendedKeyCodesSet.add(16780825);
      extendedKeyCodesSet.add(16780826);
      extendedKeyCodesSet.add(16780827);
      extendedKeyCodesSet.add(16780828);
      extendedKeyCodesSet.add(16780829);
      extendedKeyCodesSet.add(16780830);
      extendedKeyCodesSet.add(16780831);
      extendedKeyCodesSet.add(16780832);
      extendedKeyCodesSet.add(16780833);
      extendedKeyCodesSet.add(16780834);
      extendedKeyCodesSet.add(16780835);
      extendedKeyCodesSet.add(16780837);
      extendedKeyCodesSet.add(16780839);
      extendedKeyCodesSet.add(16780842);
      extendedKeyCodesSet.add(16780843);
      extendedKeyCodesSet.add(16780845);
      extendedKeyCodesSet.add(16780848);
      extendedKeyCodesSet.add(16780849);
      extendedKeyCodesSet.add(16780850);
      extendedKeyCodesSet.add(16780851);
      extendedKeyCodesSet.add(16780852);
      extendedKeyCodesSet.add(16780853);
      extendedKeyCodesSet.add(16780854);
      extendedKeyCodesSet.add(16780855);
      extendedKeyCodesSet.add(16780856);
      extendedKeyCodesSet.add(16780857);
      extendedKeyCodesSet.add(16780863);
      extendedKeyCodesSet.add(16780864);
      extendedKeyCodesSet.add(16780865);
      extendedKeyCodesSet.add(16780867);
      extendedKeyCodesSet.add(16780868);
      extendedKeyCodesSet.add(16780869);
      extendedKeyCodesSet.add(16780870);
      extendedKeyCodesSet.add(16780871);
      extendedKeyCodesSet.add(16780872);
      extendedKeyCodesSet.add(16780873);
      extendedKeyCodesSet.add(16780880);
      extendedKeyCodesSet.add(16780881);
      extendedKeyCodesSet.add(16780882);
      extendedKeyCodesSet.add(16780883);
      extendedKeyCodesSet.add(16780884);
      extendedKeyCodesSet.add(16780885);
      extendedKeyCodesSet.add(16780886);
      extendedKeyCodesSet.add(16780887);
      extendedKeyCodesSet.add(16780888);
      extendedKeyCodesSet.add(16780889);
      extendedKeyCodesSet.add(16778631);
      extendedKeyCodesSet.add(16778633);
      extendedKeyCodesSet.add(16778633);
      extendedKeyCodesSet.add(16778589);
      extendedKeyCodesSet.add(16778589);
      extendedKeyCodesSet.add(16778587);
      extendedKeyCodesSet.add(16778587);
      extendedKeyCodesSet.add(16778590);
      extendedKeyCodesSet.add(16778590);
      extendedKeyCodesSet.add(16778593);
      extendedKeyCodesSet.add(16778594);
      extendedKeyCodesSet.add(16778595);
      extendedKeyCodesSet.add(16778596);
      extendedKeyCodesSet.add(16778597);
      extendedKeyCodesSet.add(16778598);
      extendedKeyCodesSet.add(16778599);
      extendedKeyCodesSet.add(16778600);
      extendedKeyCodesSet.add(16778601);
      extendedKeyCodesSet.add(16778602);
      extendedKeyCodesSet.add(16778603);
      extendedKeyCodesSet.add(16778604);
      extendedKeyCodesSet.add(16778605);
      extendedKeyCodesSet.add(16778606);
      extendedKeyCodesSet.add(16778607);
      extendedKeyCodesSet.add(16778608);
      extendedKeyCodesSet.add(16778609);
      extendedKeyCodesSet.add(16778610);
      extendedKeyCodesSet.add(16778611);
      extendedKeyCodesSet.add(16778612);
      extendedKeyCodesSet.add(16778613);
      extendedKeyCodesSet.add(16778614);
      extendedKeyCodesSet.add(16778615);
      extendedKeyCodesSet.add(16778616);
      extendedKeyCodesSet.add(16778617);
      extendedKeyCodesSet.add(16778618);
      extendedKeyCodesSet.add(16778619);
      extendedKeyCodesSet.add(16778620);
      extendedKeyCodesSet.add(16778621);
      extendedKeyCodesSet.add(16778622);
      extendedKeyCodesSet.add(16778623);
      extendedKeyCodesSet.add(16778624);
      extendedKeyCodesSet.add(16778625);
      extendedKeyCodesSet.add(16778626);
      extendedKeyCodesSet.add(16778627);
      extendedKeyCodesSet.add(16778628);
      extendedKeyCodesSet.add(16778629);
      extendedKeyCodesSet.add(16778630);
      extendedKeyCodesSet.add(16781520);
      extendedKeyCodesSet.add(16781521);
      extendedKeyCodesSet.add(16781522);
      extendedKeyCodesSet.add(16781523);
      extendedKeyCodesSet.add(16781524);
      extendedKeyCodesSet.add(16781525);
      extendedKeyCodesSet.add(16781526);
      extendedKeyCodesSet.add(16781527);
      extendedKeyCodesSet.add(16781528);
      extendedKeyCodesSet.add(16781529);
      extendedKeyCodesSet.add(16781530);
      extendedKeyCodesSet.add(16781531);
      extendedKeyCodesSet.add(16781532);
      extendedKeyCodesSet.add(16781533);
      extendedKeyCodesSet.add(16781534);
      extendedKeyCodesSet.add(16781535);
      extendedKeyCodesSet.add(16781536);
      extendedKeyCodesSet.add(16781537);
      extendedKeyCodesSet.add(16781538);
      extendedKeyCodesSet.add(16781539);
      extendedKeyCodesSet.add(16781540);
      extendedKeyCodesSet.add(16781541);
      extendedKeyCodesSet.add(16781542);
      extendedKeyCodesSet.add(16781543);
      extendedKeyCodesSet.add(16781544);
      extendedKeyCodesSet.add(16781545);
      extendedKeyCodesSet.add(16781546);
      extendedKeyCodesSet.add(16781547);
      extendedKeyCodesSet.add(16781548);
      extendedKeyCodesSet.add(16781549);
      extendedKeyCodesSet.add(16781550);
      extendedKeyCodesSet.add(16781551);
      extendedKeyCodesSet.add(16781552);
      extendedKeyCodesSet.add(16777703);
      extendedKeyCodesSet.add(16777817);
      extendedKeyCodesSet.add(16785081);
      extendedKeyCodesSet.add(16785099);
      extendedKeyCodesSet.add(16785101);
      extendedKeyCodesSet.add(16785125);
      extendedKeyCodesSet.add(16777633);
      extendedKeyCodesSet.add(16777648);
      extendedKeyCodesSet.add(16785579);
   }
}
