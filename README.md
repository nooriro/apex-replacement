# Cell Broadcast Bug Fix (Pixel RQ1A)

Fix annoying public safety message bug in __Android 11 1st Feature Drop__ (aka `RQ1A` builds, December 2020 Update) of Google Pixel devices

## Background

When you install the __December 2020 Update__ on a Google Pixel device, __public safety messages__ (안전 안내 문자) are __no longer quietly received in South Korea__. ([See this video #1](https://youtu.be/sfjt8ZemOn0).)  
In S. Korea, public safety messages are received __5-10 times a day__ as of 2020 due to COVID-19. All of these messages are __NOT__ real emergency alerts indeed. So it's very annoying.

## How It Works

This Magisk module replaces the buggy version of __Cell Broadcast APEX Module__ (ver `300901603`) with previous bug-free version (ver `300900710`).  
With this module installed, you will receive public safety messages as quietly as before. ([See this video #2](https://youtu.be/FoXIQiNahSg).)

## Usage

Install this module on Magisk.

## Changelog

#### v1.00-20201218
* First release
