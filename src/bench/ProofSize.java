package bench;

public class ProofSize{
	private final int rawProofSize;
	private final int updateSize; 
	private final int updateProofSize;
	private final int freshnessProofSize;
	private final int freshnessProofNoOptimizationSize;
	
	public ProofSize(int raw, int update, int updateProof, int freshProof, int freshProofNoOptimization) {
		this.rawProofSize = raw;
		this.updateSize = update;
		this.updateProofSize = updateProof;
		this.freshnessProofSize = freshProof;
		this.freshnessProofNoOptimizationSize = freshProofNoOptimization;
	}

	public int getRawProofSize() {
		return rawProofSize;
	}

	public int getUpdateSize() {
		return updateSize;
	}

	public int getUpdateProofSize() {
		return updateProofSize;
	}

	public int getFreshnessProofSize() {
		return freshnessProofSize;
	}
	
	public int getFreshnessProofNoOptimizationSize() {
		return this.freshnessProofNoOptimizationSize;
	}
	
}
