var notClicked = true;
var tempElementMain;
var tempElementDetailed;
function animated(param,answer) {
    if (notClicked) {
        notClicked = false;
         param.classList.add('animate__animated', 'animate__bounceIn');
        param.addEventListener('animationend', () => {
            let p = param.firstElementChild;
            if (p.innerText.trim() == answer.trim()) {
            p.classList.add('bg-success','border-success','text-white');
            } else {
             p.classList.add('bg-danger','border-danger','text-white');   
            }
            param.lastElementChild.style.display = "block";
            
            document.querySelectorAll('.option').forEach(value => {
            if (value.innerText.trim() == answer) {
            value.classList.add('border-success');
            }
                document.getElementById('next').style.display = "block";
            });
        });
    }
}

function animatedElement(param,url) {
         param.classList.add('animate__animated', 'animate__headShake');
        param.addEventListener('animationend', () => {
            window.location.href = url;
        });
}

function detailed(param) {
    if (param.children[1].style.display == "none") {
        if (tempElementMain && tempElementDetailed) {
        tempElementMain.style.display = "none";
        tempElementDetailed.style.display = "block";
        }

        param.children[1].style.display = "flex"
        param.lastElementChild.style.display = "none";
        tempElementMain = param.children[1];
        tempElementDetailed = param.lastElementChild
    } else {
         if (tempElementMain && tempElementDetailed) {
        tempElementMain.style.display = "flex";
        tempElementDetailed.style.display = "none";
        }
        param.children[1].style.display = "none"
        param.lastElementChild.style.display = "block";
        tempElementMain = param.children[1];
        tempElementDetailed = param.lastElementChild;
    }
    
}
var board = document.getElementById("board");
var correct = document.getElementById("correct");
var question = document.getElementById("question");
function puzzelDic(param) {
    param.classList.add('animate__animated', 'animate__pulse');
        param.addEventListener('animationend', () => {
            board.innerHTML += `<span class="selected animate__animated animate__fadeIn">${param.innerText}</span>`;
            param.style.display = "none";
            if (board.innerText.length == 5) {
                document.getElementById('next').style.display = "block";
                correct.style.display = "block";
                question.classList.add('animate__heartBeat','animate__animated','complete');
                if (board.innerText.trim().toLowerCase() == "hello") {
                    correct.classList.add("text-success");
                } else {
                    correct.classList.add("text-danger");
                    question.style.border = "solid red 2px";
                }
            }
        });
}

function authAnimation(param,url,action) {
         param.classList.add('animate__animated', 'animate__headShake');
        param.addEventListener('animationend', () => {
            window.location.href = url;
        });
}