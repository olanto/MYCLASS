package isi.jg.idxvli;

import java.lang.reflect.Method;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.zip.*;
import isi.jg.idxvli.jjbg.*;
import isi.jg.idxvli.util.*;
import isi.jg.idxvli.doc.*;
import isi.jg.idxvli.word.*;
import isi.jg.idxvli.extra.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.IdxEnum.*;
import isi.jg.idxvli.cache.*;
import isi.jg.util.Timer;
import java.util.concurrent.locks.*;

/**
 * Une classe gérant le controle des chargements et sauvegarde.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class IdxIO {

    /** dernière position utilisée dans le fichier stockant l'index*/
    static final long startpos = 1;// Integer.MAX_VALUE-100000;//1; // dont use 0 = notinMemory
    private long lastbag = startpos; // dont use 0 = notinMemory

    private long lastseq = startpos; // dont use 0 = notinMemory

    private long lastposchar = startpos; // dont use 0 = notinMemory

    ObjectStorage4[] objsto;
    private RandomAccessFile bf;
    private RandomAccessFile sf;
    private RandomAccessFile pcf;
    static IdxStructure glue;
    /** pour signaler que tous les objstos sont fermés verrous ------------------------------------------*/
    private static final ReentrantReadWriteLock openRW = new ReentrantReadWriteLock();
    private static final Lock openR = openRW.readLock();
    private static final Lock openW = openRW.writeLock();

    IdxIO(IdxStructure id) { // empty constructor

        glue = id;
    }

    /** génération des id de objectstore (pour éviter de les stocker) */
    protected final static int objidx(int j) {
        return 2 * (j / OBJ_NB);
    }    // j/n car n objsto


    protected final static int objidxOnly(int j) {
        return (j / OBJ_NB);
    }    // j/n car n objsto


    protected final static int objpos(int j) {
        return 2 * (j / OBJ_NB) + 1;
    }

    protected static void signalAOpeningObjSto() {
        openR.lock();
    }

    synchronized public static void signalAClosingObjSto() {
        try {
//Il y a un bug lors de la fermeture!!          openR.unlock();
        } catch (Exception e) {
            error("during signalAClosingObjSto() finish", e);
        }

    }

    protected final void loadVectorWforFull(int j) {
        // msg("loadVectorWforFull:"+j);
        if (IDX_SAVE_POSITION) {
            // load idx
            //TimerNano trDOV= new TimerNano("read DOV",true);
            int[] res = objsto[j % OBJ_NB].readInt(objidx(j));  // lit l'index
            //long readtime=trDOV.stop(true);
            //msg("load IDX:"+res.length*4 +" [bytes]: "+ readtime+"[us]");

            //TimerNano trREG= new TimerNano("REGISTER",true);
            int[] DDOOVV = getDocOccVec(res);
            //long readtimeREG=trREG.stop(true);
            //msg("REGISTER:"+DDOOVV.length*4 +" [bytes]: "+ readtimeREG+"[us]");


            glue.indexread.registerVectorDoc(j, DDOOVV);   // enregistre dans le cache
            // load pos
            //TimerNano trPOS= new TimerNano("read POS",true);

            res = objsto[j % OBJ_NB].readInt(objpos(j));  // lit l'index
            //long readtimepos=trPOS.stop(true);
            //msg("load POS+"+res.length*4+" [bytes]: "+ readtimepos+"[us]");

            glue.indexread.registerVectorPos(j, res);   // enregistre dans le cache

        } else {
            error("*** loadVectorWforFull is not allowed when savepos=false");
        }
    }

    protected final static int[] getDocZID(int[] DODO) {  // pas de filtrage

        int[] DD = new int[DODO.length / 2];
        for (int i = 0; i < DD.length; i++) {
            DD[i] = DODO[2 * i];
        }
        return DD;
    }

    protected final int[] getDocOcc(int[] DODO) {
        int offset = DODO.length / 2;
        int[] DDOO = new int[2 * offset];
        for (int i = 0; i < offset; i++) {
            DDOO[i] = DODO[2 * i];
            DDOO[offset + i] = DODO[2 * i + 1];
        }
        return DDOO;
    }

    protected final int[] getDocOccVec(int[] DODO) {
        int offset = DODO.length / 2;
        int[] DDOOVV = new int[3 * offset];
        int offsetvec = 2 * offset;
        for (int i = 0; i < offset; i++) {
            DDOOVV[i] = DODO[2 * i];
            DDOOVV[offset + i] = DODO[2 * i + 1];
        }
        for (int i = 1; i < offset; i++) {
            DDOOVV[offsetvec + i] = DDOOVV[offsetvec + i - 1] + DODO[2 * i - 1];
        }
        return DDOOVV;
    }

    protected final void loadVectorWforBasic(int j) {
//        msg("loadVectorWforBasic:"+j+" objsto:"+j%OBJ_NB+" userkey:"+objidx(j));
        if (IDX_SAVE_POSITION) {
            int[] res = objsto[j % OBJ_NB].readInt(objidx(j));  // lit l'index
//            TimerNano t=new TimerNano("loadVectorWforBasic:"+j+" getDoc:"+res.length,true);

            switch (MODE_RANKING) {
                case NO:
                    int[] DD = getDocZID(res); // extrait les documents

                    glue.indexread.registerVectorDoc(j, DD);   // enregistre dans le cache

                    break;
                case IDFxTDF:
                case BM25:
                case BM25TWICE:
                    int[] DDOO = getDocOcc(res); // extrait les documents et occurences

                    glue.indexread.registerVectorDoc(j, DDOO);   // enregistre dans le cache

                    break;
            }
//            t.stop(false);
        } else {
            int[] res = objsto[j % OBJ_NB].readInt(objidxOnly(j));  // lit l'index

            switch (MODE_RANKING) {
                case NO:
                    glue.indexread.registerVectorDoc(j, res);   // enregistre dans le cache sans filtrage

                    break;
                case IDFxTDF:
                case BM25:
                case BM25TWICE:
//            msg("loadVectorWforBasic loaded:");
//            showVector(res);

                    int[] DDOO = getDocOcc(res); // extrait les documents et occurences

//            msg("debug in loadVectorWforBasic: "+j+" vect length " +DDOO.length/2);
//            showVector(DDOO);

                    glue.indexread.registerVectorDoc(j, DDOO);   // enregistre dans le cache

                    break;
            }
        }
    }

    /** nombre de documents dans lesquels apparait le terme j
     * @param j ième terme
     * @return nbr de documents
     */
    protected final int getOccOfW(int j) { // Count in how many documents word j appears

        if (IDX_SAVE_POSITION) {
            return objsto[j % OBJ_NB].realSize(objidx(j)) / 8;  // lit depuis objstore , /8 en byte et DODO

        } else {  // sans les posititions

            switch (MODE_RANKING) {
                case NO:
                    return objsto[j % OBJ_NB].realSize(objidxOnly(j)) / 4;  // lit depuis objstore , /4 en byte et DD

                case IDFxTDF:
                case BM25:
                case BM25TWICE:
                    //msg("getOccOfW:"+j+" - "+glue.getStringforW(j)+" - "+objsto[j%OBJ_NB].realSize(objidxOnly(j)));
                    return objsto[j % OBJ_NB].realSize(objidxOnly(j)) / 8;  // lit depuis objstore , /8 en byte et DODO

            }
        //msg(j+","+objidxOnly(j));
        }
        error("no other case");
        return -1;
    }

    protected final void saveVectorW(int j) {  // j est un cacheId

        int wordId = glue.idxtrans.getWordId(j);
        //msg("save wordid:"+wordId+" cacheid:"+j);
        saveVectorWOnePass(j, wordId);
    }

    private void saveVectorWOnePass(int j, int wordId) {
//        if (j==51){
//            msg("debug in saveVectorWOnePass+"+2*glue.indexdoc.getCountOf(j));
//            showVector(glue.indexdoc.getReferenceOn(j));
//        }

        if(MODIFY_IDX){
            if (IDX_SAVE_POSITION) {  // sauve avec les positions
        
            // save idx
            //                // controle
            //              msg("length vect:"+glue.indexdoc.v[j].length+", used:"+2*glue.idx[j]);
            //                showVector(glue.indexdoc.v[j]);

            //            if (glue.indexdoc==null)msg("indexdoc is null");
            //             if (glue.idx==null)msg("idx is null");
            objsto[wordId % OBJ_NB].append(glue.indexdoc.getReferenceOn(j), objidx(wordId), 2 * glue.indexdoc.getCountOf(j));

            //                // controle
            //                msg("app j:"+j+", allocate:"+objidx[j]);
            //                int[] res=objsto.readInt(objidx[j]);  // lit l'index
            //                showVector(res);

            //save pos

            objsto[wordId % OBJ_NB].append(glue.indexpos.getReferenceOn(j), objpos(wordId), glue.indexpos.getCountOf(j));

            glue.cntpos += glue.indexpos.getCountOf(j); //compteur global
        // release fait le travail !! glue.indexpos.setCountOf(j,0);  // remet à zéro le pointeur de position

        } else { // sauve sans les positions

            switch (MODE_RANKING) {
                case NO:
                    if (NO_IDX_ONLY_COUNT) {
                        objsto[wordId % OBJ_NB].append(new byte[0], objidxOnly(wordId), glue.indexdoc.getCountOf(j) * 4);  // seulement le nombre d'occ

                    } else {
                        objsto[wordId % OBJ_NB].append(glue.indexdoc.getReferenceOn(j), objidxOnly(wordId), glue.indexdoc.getCountOf(j));
                    }
                    break;
                case IDFxTDF:
                case BM25:
                case BM25TWICE:
                    objsto[wordId % OBJ_NB].append(glue.indexdoc.getReferenceOn(j), objidxOnly(wordId), 2 * glue.indexdoc.getCountOf(j));
            }

        }
        //glue.totsaveidx[j]+=glue.indexdoc.getCountOf(j); // met à jour le compteur global
        // release fait le travail !! glue.indexdoc.setCountOf(j,0); // remet à zéro le pointeur d'index
        glue.indexdoc.releaseVector(j);
        glue.indexpos.releaseVector(j);
    }
    }

    private void saveRawIdx(int objstoId, int[] idx, int objId, int objlength) {
        error("not implemented in MIX mode");
    }

    protected final int[] loadBag(int d) {
        try {
            //msg("Start load bag:"+d);
            //Timer t1=new Timer("Start load bag:"+d+" length:"+rdnbag[d]);
            bf.seek(glue.rdnbag[d]); // position the cursor

            int length = bf.readInt();
            int[] bag = new int[length];
            byte[] byteidx = new byte[length * 4];
            bf.read(byteidx, 0, length * 4);
            byteToint(bag, length * 4, byteidx);
            //t1.stop();
            return bag;
        } catch (Exception e) {
            System.err.println("IO error LoadBag vector");
            e.printStackTrace();
        }
        return null;
    }

    protected final void saveBag(int d, int[] bag) {
    //    System.out.println("savebag:"+d+", length:"+bag.length);
        int length = 0;
        try {
            bf.seek(lastbag);
            glue.rdnbag[d] = lastbag;
            // save idx
            length = bag.length;
            bf.writeInt(length);
            byte[] byteidx = new byte[length * 4];
            intTobyte(bag, length * 4, byteidx);
            bf.write(byteidx, 0, length * 4);
            lastbag += 4 * length + 4; // int = 4bytes

        } catch (Exception e) {
            System.err.println("IO error in savebag doc=" + d);
            e.printStackTrace();
        }
    }

    protected final int[] loadSeq(int d) {  // accéder par getSeqOfDoc in IdxStructure

        try {
            //msg("Start load seq:"+d);
            //Timer t1=new Timer("Start load seq:"+d+" length:"+doclength[d]);
            sf.seek(glue.rdnseq[d]); // position the cursor

            int length = glue.getLengthOfD(d);
            ;
            int[] seq = new int[length];
            byte[] byteidx = new byte[length * 4];
            sf.read(byteidx, 0, length * 4);
            byteToint(seq, length * 4, byteidx);
            //t1.stop();
            return seq;
        } catch (Exception e) {
            System.err.println("IO error LoadSeq vector");
            e.printStackTrace();
        }
        return null;
    }

    protected final void saveSeq(int d, int[] seq) {
        int length = 0;
        try {
            sf.seek(lastseq);
            glue.rdnseq[d] = lastseq;
            // save idx
            length = seq.length;
            byte[] byteidx = new byte[length * 4];
            intTobyte(seq, length * 4, byteidx);
            sf.write(byteidx, 0, length * 4);
            lastseq += 4 * length + 4; // int = 4bytes

        } catch (Exception e) {
            System.err.println("IO error in saveseq doc=" + d);
            e.printStackTrace();
        }
    }

    protected final int[] loadPosChar(int d) {  // accéder par getPosCharOfDoc in IdxStructure

        try {
            //msg("Start load poschar:"+d);
            //Timer t1=new Timer("Start load poschar:"+d+" length:"+doclength[d]);
            pcf.seek(glue.rdnposchar[d]); // position the cursor

            int length = glue.getLengthOfD(d);
            int[] seq = new int[length];
            byte[] byteidx = new byte[length * 4];
            pcf.read(byteidx, 0, length * 4);
            byteToint(seq, length * 4, byteidx);
            //t1.stop();
            return seq;
        } catch (Exception e) {
            System.err.println("IO error LoadPosChar vector");
            e.printStackTrace();
        }
        return null;
    }

    protected final void savePosChar(int d, int[] poschar) {
        int length = 0;
        try {
            pcf.seek(lastposchar);
            glue.rdnposchar[d] = lastposchar;
            // save idx
            length = poschar.length;
            byte[] byteidx = new byte[length * 4];
            intTobyte(poschar, length * 4, byteidx);
            pcf.write(byteidx, 0, length * 4);
            lastposchar += 4 * length + 4; // int = 4bytes

        } catch (Exception e) {
            System.err.println("IO error in saveseq doc=" + d);
            e.printStackTrace();
        }
    }

    protected final void loadindexdoc() {
        try {
            FileInputStream istream = new FileInputStream(COMMON_ROOT + "/" + currentf);
            ObjectInputStream p = new ObjectInputStream(istream);
            System.err.println("start loading");
            System.err.println("end load lastvec");
            glue.cntpos = p.readLong();
            if (IDX_WITHDOCBAG) {
                System.err.println("try to load rdnbagidx");
                lastbag = p.readLong();
                glue.rdnbag = (long[]) p.readObject();
            }
            if (IDX_MORE_INFO) {
                System.err.println("try to load docinfo");
                //glue.doclength = (int[]) p.readObject();
                glue.rdnseq = (long[]) p.readObject();
                glue.rdnposchar = (long[]) p.readObject();
            }
            if (STEM_KEEP_LIST && WORD_USE_STEMMER) {
                System.err.println("try to load stemlist");
                glue.stemList = (TreeSet<String>[]) p.readObject(); // le casting est nécessaire et génére un warning à la compilation

            }
            istream.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("IO warning file IDX is not present:" + COMMON_ROOT + "/" + currentf);

            glue.wordstable = (new Word1()).create(WORD_IMPLEMENTATION,
                    WORD_ROOT, WORD_NAME, WORD_MAXBIT, 2 * WORD_MAXLENGTH + 4);
            compactMemory("word table OK");
            // create idx
            if (MODE_IDX == IdxMode.NEW) {
                for (int i = 0; i < OBJ_NB; i++) {
                    if (IDX_SAVE_POSITION) {  // sauve avec les positions

//                        ObjectStorage4 objsto0 = (new ObjectStore4_Async()).create(OBJ_IMPLEMENTATION,
                      ObjectStorage4 objsto0 = (new ObjectStore4()).create(OBJ_IMPLEMENTATION,
                                OBJ_ROOT[i], WORD_MAXBIT + 1 - OBJ_PW2, OBJ_SMALL_SIZE);  // +1 car idx+pos -OBJ_PW2 car 2^n obsto

                        objsto0 = null; // fuite mémoire

                    } else {
//                        ObjectStorage4 objsto0 = (new ObjectStore4_Async()).create(OBJ_IMPLEMENTATION,
                        ObjectStorage4 objsto0 = (new ObjectStore4()).create(OBJ_IMPLEMENTATION,
                                OBJ_ROOT[i], WORD_MAXBIT + 0 - OBJ_PW2, OBJ_SMALL_SIZE);  // +0 car seulement idx -OBJ_PW2 car 2^n obsto

                        objsto0 = null; // fuite mémoire

                    }
                }
                MODE_IDX = IdxMode.INCREMENTAL; // maintenant on peut passer dans ce mode

            } else {
                error_fatal("mode must be :IdxMode.NEW");
            }
            compactMemory("ObjectStore OK");
            // create docs

            glue.docstable = (new Documents1()).create(DOC_IMPLEMENTATION, DOC_LANGUAGE, DOC_COLLECTION,
                    DOC_ROOT, DOC_NAME, DOC_MAXBIT, DOC_SIZE_NAME);

            compactMemory("doc table OK");
        }
        try {
            compactMemory("ready to load OK");
            bf = new RandomAccessFile(COMMON_ROOT + "/" + currentf + ".bag", "rw");
            sf = new RandomAccessFile(COMMON_ROOT + "/" + currentf + ".seq", "rw");
            pcf = new RandomAccessFile(COMMON_ROOT + "/" + currentf + ".pch", "rw");
            // restore idx
            objsto = new ObjectStorage4[OBJ_NB];
            if (MODE_IDX == IdxMode.QUERY || MODE_CONTINUE == ContinueMode.ALT) {
                COMLOG.info("open ObjectStore4 R");
                for (int i = 0; i < OBJ_NB; i++) {
                    objsto[i] = (new ObjectStore4()).open(OBJ_IMPLEMENTATION, OBJ_ROOT[i], readWriteMode.r);
                }
            }
            if ((MODE_IDX == IdxMode.INCREMENTAL || MODE_IDX == IdxMode.DIFFERENTIAL) && MODE_CONTINUE == ContinueMode.MIX) {
                COMLOG.info("open ObjectStore4 RW");
                for (int i = 0; i < OBJ_NB; i++) {
                    signalAOpeningObjSto();
                   objsto[i] = (new ObjectStore4_Async()).open(OBJ_IMPLEMENTATION, OBJ_ROOT[i], readWriteMode.rw);
                   //objsto[i] = (new ObjectStore4()).open(OBJ_IMPLEMENTATION, OBJ_ROOT[i], readWriteMode.rw);
                }
            }
            // restore words
            if (MODE_IDX == IdxMode.QUERY || MODE_CONTINUE == ContinueMode.ALT) {
                COMLOG.info("open wordstable R");
                glue.wordstable = (new Word1()).open(WORD_IMPLEMENTATION,
                        readWriteMode.r, WORD_ROOT, WORD_NAME);
            }
            if ((MODE_IDX == IdxMode.INCREMENTAL || MODE_IDX == IdxMode.DIFFERENTIAL) && MODE_CONTINUE == ContinueMode.MIX) {
                COMLOG.info("open wordstable RW");
                glue.wordstable = (new Word1()).open(WORD_IMPLEMENTATION,
                        readWriteMode.rw, WORD_ROOT, WORD_NAME);
            }
            glue.lastRecordedWord = glue.wordstable.getCount();
            glue.lastUpdatedWord = glue.lastRecordedWord;  // read=write
            // restore docs

            COMLOG.info("open docstable");
            glue.docstable = (new Documents1()).open(DOC_IMPLEMENTATION, DOC_LANGUAGE, DOC_COLLECTION, MODE_IDX, DOC_ROOT, DOC_NAME);
            glue.lastRecordedDoc = glue.docstable.getCount();
            glue.lastUpdatedDoc = glue.lastRecordedDoc;  // read=write

            compactMemory("load OK");
        } catch (Exception e) {
            System.err.println("IO warning file RND/BAG/SEQ is not present");
        //e.printStackTrace();
        }
    }

    protected final void saveindexdoc() {
        try {
            FileOutputStream ostream = new FileOutputStream(COMMON_ROOT + "/" + currentf);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeLong(glue.cntpos);
            COMLOG.info("end of idx save");
            if (IDX_WITHDOCBAG) {
                p.writeLong(lastbag);
                p.writeObject(glue.rdnbag);
                COMLOG.info("end of rdnbag save");
            }
            if (IDX_MORE_INFO) {
                //p.writeObject(glue.doclength);
                p.writeObject(glue.rdnseq);
                p.writeObject(glue.rdnposchar);
                COMLOG.info("end of docinfo save");
            }
            if (STEM_KEEP_LIST && WORD_USE_STEMMER) {
                p.writeObject(glue.stemList);
                COMLOG.info("end of stemList save");
            }
            p.flush();
            ostream.close();
            glue.docstable.close();
            COMLOG.info("end of docstable save");
            glue.wordstable.close();
            COMLOG.info("end of wordstable save");

            bf.close();
            sf.close();
            pcf.close();
            COMLOG.info("end of other save");


            COMLOG.info("START objstore close ");
            for (int i = 0; i < OBJ_NB; i++) {
                if (VERBOSE_IO) {
                    objsto[i].printStatistic();
                }
                objsto[i].close();
            }
            //openW.lock(); // attend la fin des thread (les ouvertures sont en mode reader!

            //openW.unlock();
            COMLOG.info("FINISH objstore close ");
            closeLogger();
        } catch (IOException e) {
            error("IO error in saveindexdoc", e);
        }
    }

    protected final void closeAllManager() {
        msg("before gc:" + usedMemory());
        glue.docstable.close();
        glue.docstable = null;  // pour éviter les fuites mémoires

        glue.wordstable.close();
        glue.wordstable = null;  // pour éviter les fuites mémoires

        for (int i = 0; i < OBJ_NB; i++) {  // open objectstore

            objsto[i].close();
            objsto[i] = null;
        }
        System.gc(); // fait le ménage avant s'attaquer la phase 2

        msg("after gc:" + usedMemory());

    }
}
