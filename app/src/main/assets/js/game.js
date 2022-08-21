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