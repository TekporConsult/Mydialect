window.addEventListener("offline", (event) => {
  Android.NoNetWork();
  });

  function goBack() {
    history.back();
  }

  
  var canPlay  = true;

  function timeSince(date) {

    var seconds = Math.abs(Math.floor((new Date() - date) / 1000));
    
    var interval = seconds / 31536000;
    
    if (interval > 1) {
      return Math.floor(interval) + " year(s) ago";
    }
    interval = seconds / 2592000;
    if (interval > 1) {
      return Math.floor(interval) + " month(s) ago";
    }
    interval = seconds / 86400;
    if (interval > 1) {
      return Math.floor(interval) + " day(s) ago";
    }
    interval = seconds / 3600;
    if (interval > 1) {
      return Math.floor(interval) + " hour(s) ago";
    }
    interval = seconds / 60;
    if (interval > 1) {
      return Math.floor(interval) + " minute(s) ago";
    }
    return Math.floor(seconds) + " second(s) ago";

    }


    var search = document.getElementById('search');
    var tabAction = document.getElementById('tab-actions');
    var searchSend = document.getElementById('search-send');
    var linkWord = document.getElementById('linkWord');
     var defaultLang = document.getElementById('defaultLang');
     var lang = { key: 'en', name: 'english' };
    

    async function  playSound(params)  {
    let soundPlayWord = document.getElementById('soundPlayWord');
    var word =   soundPlayWord.innerText.trim().toLowerCase()
      if(canPlay){
         toast();
        if(navigator.onLine){
          canPlay = false;
          var audio;
        
          if (lang.name == "english") {
             switch (params) {
            case 'gb':
              audio = new Audio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_gb_1.mp3`);
              audio.muted = true
              audio.play().then(data=>{
                Android.playAudio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_gb_1.mp3`,word);
              }).catch(res=>{
                audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_1.mp3`);
                audio.muted = true
                audio.play().then(data=>{
                      Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_1.mp3`,word);
                }).catch(res=>{
                  audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_3.mp3`);
                  audio.muted = true
                  audio.play().then(data=>{
                        Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_3.mp3`,word);
                  }).catch(res=>{

                    audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_1_8.mp3`);
                    audio.muted = true
                    audio.play().then(data=>{
                          Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_1_8.mp3`,word);
                    }).catch(res=>{
                      audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_2.mp3`);
                      audio.muted = true
                      audio.play().then(data=>{
                             Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_gb_2.mp3`,word);
                      }).catch(res=>{

                        audio = new Audio(`https://ssl.gstatic.com/dictionary/static/sounds/20200429/${word}--_gb_2.mp3`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://ssl.gstatic.com/dictionary/static/sounds/20200429/${word}--_gb_2.mp3`,word);
                        }).catch(res=>{

                          audio = new Audio(`https://www.collinsdictionary.com/sounds/hwd_sounds/en_gb_${word}.mp3`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.collinsdictionary.com/sounds/hwd_sounds/en_gb_${word}.mp3`,word);
                        }).catch(res => {
                           audio = new Audio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`,word);
                        }).catch(res=>{
                          toastError();
                    });
                        
                          });  

                          });  


                        });
                      });

                    });
                });
              });
              break;
            case 'gbSlow':
              audio = new Audio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_gb_2.mp3`);
              audio.muted = true
              audio.play().then(data=>{
                   Android.playAudio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_gb_2.mp3`,word);
              }).catch(res=>{
                 audio = new Audio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`,word);
                        }).catch(res=>{
                          toastError();
                    });
              });
              break;
            case 'us':
              audio = new Audio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_us_1.mp3`);
              audio.muted = true
              audio.play().then(data=>{
                    Android.playAudio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_us_1.mp3`,word);
              }).catch(res=>{
                audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_1.mp3`);
                audio.muted = true
                audio.play().then(data=>{
                     Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_1.mp3`,word);
                }).catch(res=>{
                  audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_3.mp3`);
                  audio.muted = true
                  audio.play().then(data=>{
                         Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_3.mp3`,word);
                  }).catch(res=>{

                    audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_1_8.mp3`);
                    audio.muted = true
                    audio.play().then(data=>{
                           Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_1_8.mp3`,word);
                    }).catch(res=>{
                      
                      audio = new Audio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_2.mp3`);
                      audio.muted = true
                      audio.play().then(data=>{
                             Android.playAudio(`https://audio.oxforddictionaries.com/en/mp3/${word}_us_2.mp3`,word);
                      }).catch(res=>{
    
                        audio = new Audio(`https://www.collinsdictionary.com/sounds/hwd_sounds/en_us_${word}.mp3`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.collinsdictionary.com/sounds/hwd_sounds/en_us_${word}.mp3`,word);
                        }).catch(res=>{
      
                          audio = new Audio(`https://www.translatedict.com/speak.php?word=${word}&lang=${params}`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.translatedict.com/speak.php?word=${word}&lang=${params}`,word);
                        }).catch(res=>{
                           audio = new Audio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`,word);
                        }).catch(res=>{
                          toastError();
                    });
                    });
                          });


                        });

                      });

                    });
                });
              });
              break;
            case 'usSlow':
              audio = new Audio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_us_2.mp3`);
              audio.muted = true
              audio.play().then(data=>{
                   Android.playAudio(`https://ssl.gstatic.com/dictionary/static/pronunciation/2022-03-02/audio/${word.substring(0,2)}/${word}_en_us_2.mp3`,word);
              }).catch(res=>{
                 audio = new Audio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.translatedict.com/speak.php?word=${word}&lang=${defaultLang.value =="en-gb"?"en":defaultLang.value}`,word);
                        }).catch(res=>{
                          toastError();
                    });
              });
              break;
          }
          } else {
              audio = new Audio(`https://www.translatedict.com/speak.php?word=${word}&lang=${lang.key =="en-gb"?"en":lang.key}`);
                        audio.muted = true
                        audio.play().then(data=>{
                               Android.playAudio(`https://www.translatedict.com/speak.php?word=${word}&lang=${lang.key =="en-gb"?"en":lang.key}`,word);
                        }).catch(res=>{
                          toastError();
                    });
          }
         
          canPlay = true;
        }
       
        
      }
      
    }

    var toastLive = document.getElementById('liveToast')
    var toastModel = document.getElementById('toastModel')

    var insBody = document.getElementById('insBody')
    var insToast = document.getElementById('insToast')

    var def = document.getElementById('def');
    var dated = document.getElementById('dated');
    var soundPlayWord = document.getElementById('soundPlayWord');
    
    function toast() {
      toastModel.style.display = 'block';
      new bootstrap.Toast(toastLive,{delay:2000}).show()
    }

    var toastLiveError = document.getElementById('liveToastError');
    var trendingHTML = document.getElementById('trendingHTML');
    var toastModelError = document.getElementById('toastModelError')
    
    function toastError() {
      toastModelError.style.display = 'block';
      new bootstrap.Toast(toastLiveError).show()
      canPlay = true;
    }

   async function setClip() {
    await  Android.setClipBordText(soundPlayWord.innerText.trim());
    }

    async function getClipBordText() {
      let text = Android.getClipBordText();
      search.value = text;
    tabAction.style.display = 'none';
    searchSend.style.display = 'block';
    }


    function recent(params) {
      if (params) {
        if (params[0]) {
      def.innerText = params[0].definitions[0];
      dated.innerText = timeSince(params[0].date);
      soundPlayWord.innerText = params[0].id;
      var linkWord = document.getElementById('linkWord');
    linkWord.setAttribute('href',`./lookup.html?word=${params[0].id}`)
       }
      }
      
      return params;
      
    }

async function onRun() {
    Android.cleanMediaPlayerTextReader();  
  }

  onRun();

    function BookMarks() {
     var a =  document.querySelector('ol > li > p').innerText;
      Android.BookMarks(soundPlayWord.innerText.trim(),a.trim())
    }


    function trending(params) {
      var b = "";
      for(let a  =0; a < params.length; a++){
b += `<a href="./lookup.html?word=${params[a].id}" class="rencent-items card flex-row">
<div>
    <h2 class="fs-1">${params[a].id}</h2>
    <p class="read-more">${params[a].definitions[0]}</p>
</div>

<div>
    <div class="btn">
        <svg style="width:30px;height:30px" viewBox="0 0 24 24">
            <path fill="currentColor" d="M21.41 11.58L12.41 2.58C12.05 2.22 11.55 2 11 2H4C2.89 2 2 2.89 2 4V11C2 11.55 2.22 12.05 2.59 12.41L11.58 21.41C11.95 21.77 12.45 22 13 22S14.05 21.77 14.41 21.41L14.83 21C11.6 20.9 9 18.26 9 15C9 11.69 11.69 9 15 9C18.26 9 20.9 11.6 21 14.83L21.41 14.41C21.78 14.05 22 13.55 22 13C22 12.44 21.77 11.94 21.41 11.58M5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7M15.11 10.61C17.61 10.61 19.61 12.61 19.61 15.11C19.61 16 19.36 16.82 18.92 17.5L22 20.61L20.61 22L17.5 18.93C16.8 19.36 16 19.61 15.11 19.61C12.61 19.61 10.61 17.61 10.61 15.11S12.61 10.61 15.11 10.61M15.11 12.61C13.73 12.61 12.61 13.73 12.61 15.11S13.73 17.61 15.11 17.61 17.61 16.5 17.61 15.11 16.5 12.61 15.11 12.61" /> </svg>
    </div>
</div>
</a>`
      }
      trendingHTML.innerHTML = b;
      return params;
      
    }

    var supportedLanguage = [
      {language:"Arabic",
        id:"ar",
        discription:"the Semitic language of the Arabs, spoken by some 150 million people throughout the Middle East and North Africa.",
        flag:'https://www.translatedict.com/images/flags/arabic.png'},
        
        {language:"Azerbaijani",
        id:"az",
        discription:"the Turkic language spoken by over 14 million people in Azerbaijan and adjacent regions.",
        flag:'https://www.translatedict.com/images/flags/azerbaijani.png'},
        
       
        {language:"Ewe",
        id:"ew",
        discription:"the language of the Ewe, belonging to the Kwa group.",
        flag:'./images/ewe.png'},
        
        {language:"English",
        id:"en",
        discription:"the language of England, widely used in many varieties throughout the world.",
        flag:'https://www.translatedict.com/images/english.png'},
        
        {language:"Fante",
        id:"fa",
        discription:"the dialect of Akan spoken by the Fante.",
        flag:'./images/fante.jpg'},
        
        {language:"French",
        id:"fr",
        discription:"German is a West Germanic language of the Indo-European language family, mainly spoken in Central Europe.",
        flag:'https://www.translatedict.com/images/flags/french.png'},
        {language:" German",
        id:"de",
        discription:"the ancient or modern language of Greece, the only representative of the Hellenic branch of the Indo-European family.",
        flag:'https://www.translatedict.com/images/flags/german.png'},
        
        {language:"Greek",
        id:"el",
        discription:"the ancient or modern language of Greece, the only representative of the Hellenic branch of the Indo-European family.",
        flag:'https://www.translatedict.com/images/flags/greek.png'},
        
       
        {language:"Hindi",
        id:"hi",
        discription:"an Indic language of northern India, derived from Sanskrit and written in the Devanagari script.",
        flag:'https://www.translatedict.com/images/flags/hindi.png'},
        
        {language:"Indonesian",
        id:"id",
        discription:"the group of Austronesian languages, closely related to Malay, which are spoken in Indonesia and neighbouring islands.",
        flag:'https://www.translatedict.com/images/flags/indonesian.png'},
        {language:" Italian",
        id:"it",
        discription:"the Romance language of Italy, descended from Latin and with roughly 60 million speakers worldwide. It is also one of the official languages of Switzerland.",
        flag:'https://www.translatedict.com/images/flags/italian.png'},
        
        {language:"Japanese",
        id:"ja",
        discription:"the language of Japan, spoken by almost all of its population.",
        flag:'https://www.translatedict.com/images/flags/japanese.png'},
        
        {language:"Korean",
        id:"ko",
        discription:"the language of Korea, which has roughly 68 million speakers worldwide. It has its own writing system, and is now generally regarded as distantly related to Japanese.",
        flag:'https://www.translatedict.com/images/flags/korean.png'},

         {language:"Latvian",
        id:"lv",
        discription:".",
        flag:'https://www.translatedict.com/images/flags/Latvia.png'},
        


        {language:"Mandarin",
        id:"zh-CN",
        discription:"the standard literary and official form of Chinese, spoken by over 730 million people.",
        flag:'https://www.translatedict.com/images/flags/chinese-simplified.png'},
        
        {language:"Portuguese",
        id:"pt",
        discription:"the language of Portugal and Brazil, a Romance language spoken by about 160 million people.",
        flag:'https://www.translatedict.com/images/flags/portuguese.png'},


        {language:"Romaian",
        id:"ro",
        discription:"the language of Russia, an Eastern Slavic language written in the Cyrillic alphabet and spoken by over 130 million people.",
        flag: 'https://www.translatedict.com/images/flags/romania.png'
      },
        
        {language:"Russian",
        id:"ru",
        discription:"the language of Russia, an Eastern Slavic language written in the Cyrillic alphabet and spoken by over 130 million people.",
        flag:'https://www.translatedict.com/images/flags/russian.png'},

        {language:"Spanish",
        id:"es",
        discription:"a Romance language spoken in Spain and in much of Central and South America (except Brazil) and several other countries.",
        flag:'https://www.translatedict.com/images/flags/spanish.png'},


        {language:"Twi",
        id:"twi",
        discription:"a language of an Akan-speaking people of Ghana.",
        flag:'./images/twi.webp'},
        {language:" Ukrainian",
        id:"uk",
        discription:"Ukrainian is an East Slavic language of the Indo-European language family. It is the native language of about 40 million people and the official state.",
        flag:'https://www.translatedict.com/images/flags/ukranian.png'},

        ]

        async function recordListing() {
          toast();
          await Android.recordSound();

         }
       

       