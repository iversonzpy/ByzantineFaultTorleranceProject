package bftproject.ycsbapp.occ;


import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SerializeExecutor {

    public CacheWrapper cache;
    public OptConcurrencyControl occ;
    private int startTnc;
    private Queue<CacheWrapper> backupQueue;


    public SerializeExecutor(CacheWrapper cache, OptConcurrencyControl occ){
        this.cache = cache;
        this.occ = occ;
        this.startTnc = occ.getTnc();
        this.backupQueue = new LinkedList<>();
    }

    public CacheWrapper getCache() {
        return cache;
    }

    public void setCache(CacheWrapper cache) {
        this.cache = cache;
    }

    public OptConcurrencyControl getOcc() {
        return occ;
    }

    public void setOcc(OptConcurrencyControl occ) {
        this.occ = occ;
    }


    public boolean validate() {
        if (this.cache.getWriteSet().size() == 0){
            return true;
        }
        int finishTn = occ.getTnc();
        boolean isValid = true;
        for (int tn = this.startTnc + 1; tn < finishTn + 1; tn++) {
            CacheWrapper other_caches = occ.getCacheWrapperByTn(tn);
            Set<String> readSet  = this.cache.getReadSet();
            Set<String> writeSet =  other_caches.getWriteSet();
            for (String key : writeSet){
                if (readSet.contains(key)) {
                    isValid = false;
                    this.backupQueue.add(this.cache);
                    break;
                }
            }
        }
        if (isValid){
            occ.commitAfterValidate(this.cache);
        }
        else {
            abortAndClear();
        }
        return isValid;
    }

    private void abortAndClear(){
        while(this.backupQueue.peek() != null){
            CacheWrapper cachedWrapper = this.backupQueue.poll();
            cachedWrapper.abort();
        }
    }


}
