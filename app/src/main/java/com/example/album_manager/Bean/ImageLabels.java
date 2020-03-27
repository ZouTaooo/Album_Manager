package com.example.album_manager.Bean;

import java.util.List;

public class ImageLabels {

    /**
     * Response : {"Labels":[{"Name":"塔楼","FirstCategory":"场景","SecondCategory":"建筑","Confidence":81},{"Name":"夜晚","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":79},{"Name":"天际线","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":77},{"Name":"城市景观","FirstCategory":"场景","SecondCategory":"其他","Confidence":77},{"Name":"城市","FirstCategory":"场景","SecondCategory":"生活/娱乐场所","Confidence":72},{"Name":"都市","FirstCategory":"场景","SecondCategory":"其他","Confidence":69}],"CameraLabels":[{"Name":"夜景","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":92},{"Name":"城市","FirstCategory":"场景","SecondCategory":"建筑","Confidence":3},{"Name":"游乐园","FirstCategory":"场景","SecondCategory":"生活/娱乐场所","Confidence":2},{"Name":"大厦","FirstCategory":"场景","SecondCategory":"建筑","Confidence":1},{"Name":"桥","FirstCategory":"场景","SecondCategory":"建筑","Confidence":0}],"AlbumLabels":[{"Name":"夜景","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":93},{"Name":"塔","FirstCategory":"场景","SecondCategory":"建筑","Confidence":82},{"Name":"城市","FirstCategory":"场景","SecondCategory":"建筑","Confidence":5}],"RequestId":"e6d685b1-d898-4dc9-a545-cfeb3a988da8"}
     */

    private ResponseBean Response;

    public ResponseBean getResponse() {
        return Response;
    }

    public void setResponse(ResponseBean Response) {
        this.Response = Response;
    }

    public static class ResponseBean {
        /**
         * Labels : [{"Name":"塔楼","FirstCategory":"场景","SecondCategory":"建筑","Confidence":81},{"Name":"夜晚","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":79},{"Name":"天际线","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":77},{"Name":"城市景观","FirstCategory":"场景","SecondCategory":"其他","Confidence":77},{"Name":"城市","FirstCategory":"场景","SecondCategory":"生活/娱乐场所","Confidence":72},{"Name":"都市","FirstCategory":"场景","SecondCategory":"其他","Confidence":69}]
         * CameraLabels : [{"Name":"夜景","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":92},{"Name":"城市","FirstCategory":"场景","SecondCategory":"建筑","Confidence":3},{"Name":"游乐园","FirstCategory":"场景","SecondCategory":"生活/娱乐场所","Confidence":2},{"Name":"大厦","FirstCategory":"场景","SecondCategory":"建筑","Confidence":1},{"Name":"桥","FirstCategory":"场景","SecondCategory":"建筑","Confidence":0}]
         * AlbumLabels : [{"Name":"夜景","FirstCategory":"场景","SecondCategory":"自然风光","Confidence":93},{"Name":"塔","FirstCategory":"场景","SecondCategory":"建筑","Confidence":82},{"Name":"城市","FirstCategory":"场景","SecondCategory":"建筑","Confidence":5}]
         * RequestId : e6d685b1-d898-4dc9-a545-cfeb3a988da8
         */

        private String RequestId;
        private List<LabelsBean> Labels;
        private List<CameraLabelsBean> CameraLabels;
        private List<AlbumLabelsBean> AlbumLabels;

        public String getRequestId() {
            return RequestId;
        }

        public void setRequestId(String RequestId) {
            this.RequestId = RequestId;
        }

        public List<LabelsBean> getLabels() {
            return Labels;
        }

        public void setLabels(List<LabelsBean> Labels) {
            this.Labels = Labels;
        }

        public List<CameraLabelsBean> getCameraLabels() {
            return CameraLabels;
        }

        public void setCameraLabels(List<CameraLabelsBean> CameraLabels) {
            this.CameraLabels = CameraLabels;
        }

        public List<AlbumLabelsBean> getAlbumLabels() {
            return AlbumLabels;
        }

        public void setAlbumLabels(List<AlbumLabelsBean> AlbumLabels) {
            this.AlbumLabels = AlbumLabels;
        }

        public static class LabelsBean {
            /**
             * Name : 塔楼
             * FirstCategory : 场景
             * SecondCategory : 建筑
             * Confidence : 81
             */

            private String Name;
            private String FirstCategory;
            private String SecondCategory;
            private int Confidence;

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getFirstCategory() {
                return FirstCategory;
            }

            public void setFirstCategory(String FirstCategory) {
                this.FirstCategory = FirstCategory;
            }

            public String getSecondCategory() {
                return SecondCategory;
            }

            public void setSecondCategory(String SecondCategory) {
                this.SecondCategory = SecondCategory;
            }

            public int getConfidence() {
                return Confidence;
            }

            public void setConfidence(int Confidence) {
                this.Confidence = Confidence;
            }
        }

        public static class CameraLabelsBean {
            /**
             * Name : 夜景
             * FirstCategory : 场景
             * SecondCategory : 自然风光
             * Confidence : 92
             */

            private String Name;
            private String FirstCategory;
            private String SecondCategory;
            private int Confidence;

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getFirstCategory() {
                return FirstCategory;
            }

            public void setFirstCategory(String FirstCategory) {
                this.FirstCategory = FirstCategory;
            }

            public String getSecondCategory() {
                return SecondCategory;
            }

            public void setSecondCategory(String SecondCategory) {
                this.SecondCategory = SecondCategory;
            }

            public int getConfidence() {
                return Confidence;
            }

            public void setConfidence(int Confidence) {
                this.Confidence = Confidence;
            }
        }

        public static class AlbumLabelsBean {
            /**
             * Name : 夜景
             * FirstCategory : 场景
             * SecondCategory : 自然风光
             * Confidence : 93
             */

            private String Name;
            private String FirstCategory;
            private String SecondCategory;
            private int Confidence;

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getFirstCategory() {
                return FirstCategory;
            }

            public void setFirstCategory(String FirstCategory) {
                this.FirstCategory = FirstCategory;
            }

            public String getSecondCategory() {
                return SecondCategory;
            }

            public void setSecondCategory(String SecondCategory) {
                this.SecondCategory = SecondCategory;
            }

            public int getConfidence() {
                return Confidence;
            }

            public void setConfidence(int Confidence) {
                this.Confidence = Confidence;
            }
        }
    }
}
