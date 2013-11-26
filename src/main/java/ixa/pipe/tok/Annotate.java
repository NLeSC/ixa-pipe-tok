/*
 * Copyright 2013 Rodrigo Agerri

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package ixa.pipe.tok;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.WF;
import ixa.pipe.seg.SentenceSegmenter;

import java.io.IOException;

public class Annotate {

  /**
   * This function takes the original input text and cleans extra newlines and
   * spaces creating the input text for the sentence segmenter and the tokenizer
   * 
   * @param String
   *          text
   * @return String text
   */

  public String buildText(String text) {
    text = text.replaceAll("(<JA><JA>)+", "<P>");
    text = text.replaceAll("<JA>", " ");
    text = text.replaceAll("\\s+", " ");
    text = text.trim();
    return text;
  }

  /**
   * This method performs Sentence Detection and Tokenization to produce
   * tokenized text by sentences represented in KAF format.
   * 
   * For every line of text the method receives, it creates an array of
   * segmented sentences, and an array of Tokens.
   * 
   * It fills the kaf object with the word forms element <wf> corresponding to
   * each of the tokens.
   * 
   * @param String
   *          text
   * @param SentenceSegmenter
   *          sentDetector
   * @param Tokenizer
   *          toker
   * @param KAF
   *          object. This object is used to take the output data and convert it
   *          to KAF, returning an XML document in a string.
   */

  public void annotateTokensToKAF(String text, String lang,
      SentenceSegmenter sentDetector, Tokenizer toker, KAFDocument kaf)
      throws IOException {

    int noSents = 0;
    //offset counters
    int offsetCounter = 0;
    int current_index = 0;
    int previous_index = 0;
    
    //build text to be tokenized
    text = buildText(text);

    // this creates the actual paragraphs to be passed to the sentence detector
    String[] lines = text.split("<P>");

    for (String line : lines) {

      line = line.trim();
      String[] sentences = sentDetector.segmentSentence(line);
      
      // get linguistic annotations
      for (String sent : sentences) {
        // clean extra spaces
        sent = sent.trim();
        sent = sent.replaceAll("\\s+", " ");

        // tokenize each sentence
        String[] tokens = sent.split(" ");
        // get sentence counter
        noSents = noSents + 1;
        
        for (int i = 0; i < tokens.length; i++) {
          // get offsets
          current_index = line.indexOf(tokens[i], previous_index);
          int offset = offsetCounter + current_index;
          WF wf = kaf.newWF(tokens[i], offset);
          wf.setSent(noSents);
          previous_index = current_index + tokens[i].length();
        }
      }
      offsetCounter += line.length();
    }
  }
  
  /**
   * This method performs Sentence Detection and Tokenization to produce
   * tokenized text by sentences represented in KAF format.
   * 
   * For every line of text the method receives, it creates an array of
   * segmented sentences, and an array of Tokens.
   * 
   * It fills the kaf object with the word forms element <wf> corresponding to
   * each of the tokens.
   * 
   * @param String
   *          text
   * @param SentenceSegmenter
   *          sentDetector
   * @param Tokenizer
   *          toker
   * @param KAF
   *          object. This object is used to take the output data and convert it
   *          to KAF, returning an XML document in a string.
   */

  int tokSents = 0;
  int tokOffsetCounter = 0;
  int tokCurrent_index = 0;
  int tokPrevious_index = 0;
  
  public void tokenizedTextToKAF(String text, String lang, Tokenizer toker, KAFDocument kaf)
      throws IOException {

    // this creates the actual sentences to be passed to the sentence detector
    String[] sentences = text.split("<JA>");

    for (String sent : sentences) {
        // clean extra spaces
        sent = sent.trim();
        sent = sent.replaceAll("\\s+", " ");
        // System.out.println(sent);

        // "tokenize" an already tokenized sentence
        //String[] tokens = toker.tokenize(sent, lang);
        String[] tokens = sent.split(" ");
        // get sentence counter
        tokSents = tokSents + 1;
       
        for (int i = 0; i < tokens.length; i++) {
          // get offsets
          tokCurrent_index = sent.indexOf(tokens[i], tokPrevious_index);
          int offset = tokOffsetCounter + tokCurrent_index;
          WF wf = kaf.newWF(tokens[i], offset);
          wf.setSent(tokSents);
          tokPrevious_index = tokCurrent_index + tokens[i].length();
        }
        tokOffsetCounter += sent.length();
      }
      
  }
}
